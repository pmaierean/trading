package com.maiereni.batch.analysis.financial.processor.impl;

import am.ik.yfinance4j.Interval;

import am.ik.yfinance4j.Period;
import am.ik.yfinance4j.Ticker;
import am.ik.yfinance4j.YFinance;
import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.HistoryRecord;
import com.maiereni.batch.analysis.financial.bo.TickerDataProcessorRequest;
import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import com.maiereni.batch.analysis.financial.data.dao.StocksDao;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityHistory;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import com.maiereni.batch.analysis.financial.processor.TickerDataProcessor;
import com.maiereni.batch.analysis.financial.util.DateUtils;
import com.maiereni.batch.analysis.financial.util.RecordConverter;
import com.maiereni.batch.analysis.financial.util.SecurityMasterFilter;
import com.opencsv.bean.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.Instant;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
@Component
@Slf4j
public class TickerDataProcessorImpl implements TickerDataProcessor {
    public static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral('-')
            .appendValue(ChronoField.MONTH_OF_YEAR, 2)
            .appendLiteral("-")
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral("T00:00:00Z")
            .toFormatter();
    private final StocksDao stocksDao;
    private final RecordConverter recordConverter = new RecordConverter();

    public TickerDataProcessorImpl(StocksDao stocksDao) {
        this.stocksDao = stocksDao;
    }

    @Override
    public List<SecurityMaster> getTickers(String filter) {
        return SecurityMasterFilter.build(filter).filter(stocksDao.getTickers());
    }

    @Override
    public void downloadTickerData(TickerDataProcessorRequest request) throws Exception {
        Assert.notNull(request, "Request must not be null");
        Assert.notNull(request.getTicker(), "Ticker must not be null");
        Assert.notNull(request.getEndDate(), "The end date must not be null");
        if (DateUtils.isWeekend(request.getEndDate())) {
            log.info("The end date is a weekend");
        }
        else {
            File tickerFile = null;
            if (StringUtils.isNotBlank(request.getBackupFolder())) {
                File dir = new File(request.getBackupFolder());
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        throw new Exception("Can't create directory " + dir.getAbsolutePath());
                    }
                }
                tickerFile = new File(dir, request.getTicker().getTicker() + "." + request.getTicker().getExchange() + ".csv");
            }
            LocalDateTime dateTime = stocksDao.getMostRecentDate(request.getTicker());
            if (dateTime == null) {
                log.debug("No most recent date found");
                download(request.getTicker(), tickerFile, request.getStartDate(), request.getEndDate());
            } else if (dateTime.isBefore(request.getEndDate())) {
                log.debug("The most recent date of the ticker is {}", dateTime.format(DATE_FORMATTER));
                LocalDateTime date = dateTime.plusDays(1L);
                while(DateUtils.isWeekend(date)) {
                    date = date.plusDays(1L);
                }
                download(request.getTicker(), tickerFile, date, request.getEndDate());
            } else {
                log.debug("Data for ticker is up to date");
            }
        }
    }

    private void download(SecurityMaster securityMaster, File tickerFile, LocalDateTime startDate, LocalDateTime endDate)
         throws Exception {
        if (startDate == null || startDate.isBefore(endDate)) {
            LocalDateTime begin = startDate;
            log.debug("Get data for ticker {}.{} for period between {} and {}", securityMaster.getTicker(), securityMaster.getExchange(), startDate != null ? startDate.format(DATE_FORMATTER) : "beginning", endDate.format(DATE_FORMATTER));
            List<TickerRecord> toSave = new ArrayList<>();
            boolean isAppend = false;
            if (begin != null && tickerFile != null) { // If there is a cached local file
                List<TickerRecord> fromFile = readFromFile(tickerFile);
                if (fromFile != null) {
                    isAppend = true;
                    for (TickerRecord record : fromFile) { // Load all records the match the timeframe
                        LocalDateTime l = LocalDateTime.ofInstant(record.getTimestamp(), ZoneId.systemDefault());
                        if (l.isBefore(endDate.plusDays(1L)) && l.isAfter(startDate.minusDays(1L))) {
                            toSave.add(record);
                            if (l.isAfter(begin)) {
                                begin = l;
                            }
                        }
                    }
                }
                if (!toSave.isEmpty()) {
                    log.debug("Loaded from cache a number of {} records", toSave.size());
                    begin = begin.plusDays(1L);
                    while (DateUtils.isWeekend(begin)) {
                        begin = begin.plusDays(1L);
                    }
                    log.debug("Beginning date to cover: {}", begin.format(DATE_FORMATTER));
                }
            }
            String symbol = securityMaster.getTicker();
            if (StringUtils.isNotBlank(securityMaster.getSecurityType())) {
                if (securityMaster.getSecurityType().equals("Business Trust") || securityMaster.getSecurityType().equals("Trading") ||
                    securityMaster.getSecurityType().equals("FI Trust")) {
                    symbol = symbol + "-UN";
                }
            }
            symbol = switch (securityMaster.getExchange()) {
                case "TSX" -> symbol + ".TO";
                case "TSXV" -> symbol + ".V";
                default -> symbol;
            };
            List<TickerRecord> downloaded = downloadTickerHistory(symbol, begin, endDate);
            if (tickerFile != null) {
                backupToCache(downloaded, tickerFile, isAppend);
            }
            log.debug("Downloaded {} records", downloaded.size());
            toSave.addAll(downloaded);
            if (toSave.isEmpty()) {
                log.debug("No data to save for ticker {} at {}", securityMaster.getTicker(), securityMaster.getExchange());
            }
            else {
                List<SecurityHistory> securityHistories = convert(securityMaster, toSave);
                log.debug("Saving {} security histories", securityHistories.size());
                stocksDao.save(securityHistories);
            }
        }
    }

    private void backupToCache(List<TickerRecord> tickerRecords, File tickerFile, boolean isAppend) throws Exception {
        try (FileWriter writer = new FileWriter(tickerFile, true);
             StringWriter sw = new StringWriter()) {
            ColumnPositionMappingStrategy<TickerRecord> recordMappingStrategy = new ColumnPositionMappingStrategy<>();
            recordMappingStrategy.setType(TickerRecord.class);
            StatefulBeanToCsv<TickerRecord> beanToCsv = new StatefulBeanToCsvBuilder<TickerRecord>(sw)
                    .withMappingStrategy(recordMappingStrategy)
                    .build();
            beanToCsv.write(tickerRecords);
            if (isAppend) {
                log.debug("Append data to existing file");
            }
            else {
                log.debug("Save date to new file");
                writer.write("\"ADJ CLOSE\",\"CLOSE\",\"DIVIDENDS\",\"HIGH\",\"LOW\",\"OPEN\",\"STOCK SPLIT\",\"TIMESTAMP\",\"VOLUME\"\r\n");
            }
            writer.write(sw.toString());
            log.debug("The history has been downloaded and saved to the CSV file at {}", tickerFile.getPath());
        }
    }
    private List<SecurityHistory> convert(SecurityMaster securityMaster, List<TickerRecord> tickerRecords) {
        List<SecurityHistory> securityHistories = new ArrayList<>();
        for (TickerRecord record : tickerRecords) {
            securityHistories.add(convert(securityMaster, record));
        }
        return securityHistories;
    }

    private SecurityHistory convert(SecurityMaster securityMaster, TickerRecord record) {
        SecurityHistory securityHistory = new SecurityHistory();
        securityHistory.setSecurityMaster(securityMaster);
        securityHistory.setAdjClose(record.getAdjClose());
        securityHistory.setClose(record.getClose());
        securityHistory.setOpen(record.getOpen());
        securityHistory.setHigh(record.getHigh());
        securityHistory.setLow(record.getLow());
        securityHistory.setDividends(record.getDividends());
        securityHistory.setStockSplit(record.getStockSplits());
        securityHistory.setTimestamp(LocalDateTime.ofInstant(record.getTimestamp(), ZoneId.systemDefault()));
        securityHistory.setVolume(BigDecimal.valueOf(record.getVolume()));
        securityHistory.setCreationDate(LocalDateTime.now());
        securityHistory.setCreatedBy("Batch");
        securityHistory.setStatus(1);
        return securityHistory;
    }

    private List<TickerRecord> readFromFile(File destFile) {
        if (destFile!= null && destFile.exists()) {
            try (Reader reader = Files.newBufferedReader(destFile.toPath())) {
                return new CsvToBeanBuilder<TickerRecord>(reader)
                        .withType(TickerRecord.class)
                        .withIgnoreLeadingWhiteSpace(true)
                        .withSkipLines(1)
                        .build()
                        .parse();

            } catch (Exception e) {
                log.error("Failed to read from file " + destFile.getPath(), e);
            }
        }
        return null;
    }

    protected List<TickerRecord> downloadTickerHistory(
            String symbol,
            LocalDateTime startDate,
            LocalDateTime endDate) throws Exception {
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();

        YFinance yf = new YFinance(restClient);
        Ticker ticker = yf.ticker(symbol);
        ChartRequest.Builder builder = null;
        if (startDate != null) {
            String sEnd = endDate.format(DATE_FORMATTER);
            String sStart = startDate.format(DATE_FORMATTER);
            log.debug("Download the history of the ticker {} between {} - {}", symbol, sStart, sEnd);
            builder = ChartRequest.builder()
                    .start(Instant.parse(sStart))
                    .end(Instant.parse(sEnd))
                    .interval(Interval.ONE_DAY);
        }
        else {
            log.debug("Download the history of the ticker {}", symbol);
            builder = ChartRequest.builder()
                    .period(Period.FIVE_YEARS)
                    .interval(Interval.ONE_DAY);
        }
        ChartRequest request = builder.build();
        List<HistoryRecord> range = ticker.history(request);
        return recordConverter.getTickerHistory(range);
    }
}
