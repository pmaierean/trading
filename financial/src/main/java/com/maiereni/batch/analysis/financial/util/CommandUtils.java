package com.maiereni.batch.analysis.financial.util;

import am.ik.yfinance4j.Interval;
import am.ik.yfinance4j.Period;
import com.maiereni.batch.analysis.financial.bo.Argument;
import com.maiereni.batch.analysis.financial.bo.MacdRequest;
import com.maiereni.batch.analysis.financial.bo.TickerRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Slf4j
public class CommandUtils {
    /**
     * Gets the file name of the ticker downloaded file
     * @param tickerRequest the argument
     * @return the file name
     * @throws Exception failed to detect the file
     */
    public File getDownloadedCSVFile(TickerRequest tickerRequest) throws Exception {
        if (tickerRequest == null) {
            throw new Exception("TickerHistory is null");
        }
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("ticker", tickerRequest.getTicker());
        valuesMap.put("sourceDir", getSourceDir(tickerRequest).getPath());
        valuesMap.put("startDate", getStartDateString(tickerRequest));
        valuesMap.put("endDate", getEndDateString(tickerRequest));
        if (tickerRequest.getInterval() != null) {
            valuesMap.put("interval", tickerRequest.getInterval().toString());
        }
        else {
            valuesMap.put("interval", Interval.ONE_DAY.toString());
        }
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String name = sub.replace(tickerRequest.getSourceFilePattern());
        return new File(name);
    }

    /**
     * Get the output file for MACD histogram
     * @param macdRequest the request
     * @return the file
     * @throws Exception failure
     */
    public File getMacdHistogramCSVFile(MacdRequest macdRequest) throws Exception {
        if (macdRequest == null) {
            throw new Exception("MacdRequest is null");
        }
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("ticker", macdRequest.getTicker());
        valuesMap.put("destinationDir", getDestinationDir(macdRequest).getPath());
        valuesMap.put("startDate", getStartDateString(macdRequest));
        valuesMap.put("endDate", getEndDateString(macdRequest));
        valuesMap.put("type", "hist");
        if (macdRequest.getInterval() != null) {
            valuesMap.put("interval", macdRequest.getInterval().toString());
        }
        else {
            valuesMap.put("interval", Interval.ONE_DAY.toString());
        }
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        String name = sub.replace(macdRequest.getResultFilePattern());
        return new File(name);
    }

    public String getEndDateString(TickerRequest tickerHistory) throws Exception {
        Instant endDate = getEndDate(tickerHistory);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                .withZone(ZoneId.of("UTC"));
        return formatter.format(endDate);
    }

    public Instant getEndDate(TickerRequest tickerHistory) throws Exception {
        Instant endDate = null;
        if (tickerHistory.getEndDate() == null) {
            endDate = Instant.now();
        }
        else {
            endDate = Instant.parse(tickerHistory.getEndDate());
        }
        return endDate;
    }

    public String getStartDateString(TickerRequest tickerHistory) throws Exception {
        Instant start = getStartDate(tickerHistory);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                .withZone(ZoneId.of("UTC"));
        return formatter.format(start);
    }

    public Instant getStartDate(TickerRequest tickerHistory) throws Exception {
        Instant start = null;
        if (tickerHistory.getStartDate() == null) {
            Instant end = getEndDate(tickerHistory);
            if (tickerHistory.getPeriod() != null) {
                start = getStartDate(tickerHistory.getPeriod(), end);
            }
            else {
                start = getStartDate(Period.YEAR_TO_DATE, end);
            }
        }
        else {
            start = Instant.parse(tickerHistory.getStartDate());
        }
        return start;
    }

    public Instant getStartDate(Period period, Instant ref) throws Exception {
        Instant startDate = ref == null ? Instant.now() : ref;
        switch (period) {
            case Period.ONE_DAY ->  {
                startDate = startDate.minus(1, ChronoUnit.DAYS);
            }
            case Period.FIVE_DAYS -> {
                startDate = startDate.minus(5, ChronoUnit.DAYS);
            }
            case Period.ONE_MONTH ->   {
                startDate = startDate.minus(1, ChronoUnit.MONTHS);
            }
            case Period.THREE_MONTHS -> {
                startDate = startDate.minus(3, ChronoUnit.MONTHS);
            }
            case Period.SIX_MONTHS -> {
                startDate = startDate.minus(6, ChronoUnit.MONTHS);
            }
            case Period.ONE_YEAR -> {
                startDate = startDate.minus(12, ChronoUnit.MONTHS);
            }
            case Period.TWO_YEARS -> {
                startDate = startDate.minus(2, ChronoUnit.YEARS);
            }
            case Period.TEN_YEARS -> {
                startDate = startDate.minus(10, ChronoUnit.YEARS);
            }
            case Period.YEAR_TO_DATE ->  {
                ZonedDateTime zdt = startDate.atZone(ZoneId.of("UTC"));
                ZonedDateTime startOfYear = zdt.withDayOfYear(1).toLocalDate().atStartOfDay(zdt.getZone());
                startDate = startOfYear.toInstant();
            }
            default -> {
                throw new Exception("Invalid period");
            }
        }
        return startDate;
    }

    public File getDestinationDir(Argument argument) throws Exception {
        File ret = null;
        if (StringUtils.isNotBlank(argument.getDestinationDir())) {
            ret = new File(argument.getSourceDir());
        }
        else {
            ret = new File("./results");
        }
        if (!ret.exists()) {
            if (!ret.mkdirs()) {
                throw new Exception("Can't create directory: " + argument.getDestinationDir());
            }
        }
        return ret;
    }


    public File getSourceDir(Argument argument) throws Exception {
        File ret = null;
        if (StringUtils.isNotBlank(argument.getSourceDir())) {
            ret = new File(argument.getSourceDir());
        }
        else {
            ret = new File("./sources");
        }
        if (!ret.exists()) {
            if (!ret.mkdirs()) {
                throw new Exception("Can't create directory: " + argument.getSourceDir());
            }
        }
        return ret;
    }
}
