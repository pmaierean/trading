package com.maiereni.batch.analysis.financial.command;

import am.ik.yfinance4j.Interval;
import am.ik.yfinance4j.Ticker;
import am.ik.yfinance4j.YFinance;
import am.ik.yfinance4j.chart.ChartRequest;
import am.ik.yfinance4j.chart.HistoryRecord;
import com.maiereni.batch.analysis.financial.bo.TickerRequest;
import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import com.maiereni.batch.analysis.financial.util.CommandUtils;
import com.maiereni.batch.analysis.financial.util.RecordConverter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * A command class that downloads the ticker history
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Order(1)
@Component
@Slf4j
public class TickerHistoryDownloader extends BaseCommand {
    private final CommandUtils commandUtils = new CommandUtils();
    private final RecordConverter recordConverter = new RecordConverter();
    /**
     * Downloader of a ticker history
     * @param properties the properties of the command
     * @throws Exception
     */
    @Override
    protected void process(Map<String, String[]> properties) throws Exception {
        TickerRequest tickerHistory = new TickerRequest();
        BeanUtils.populate(tickerHistory, properties);
        if (StringUtils.isNotBlank(tickerHistory.getTicker())) {
            File sourceFile = commandUtils.getDownloadedCSVFile(tickerHistory);
            if (!sourceFile.exists()) {
                downloadTickerHistory(tickerHistory, sourceFile);
            }
            else {
                log.info("Ticker history file exists at {}", sourceFile.getPath());
            }
        }
        log.info("TickerHistoryDownloader process completed");
    }

    protected void downloadTickerHistory(TickerRequest tickerHistory, File destFile) throws Exception {
        RestClient restClient = RestClient.builder()
                .requestFactory(new JdkClientHttpRequestFactory())
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();

        YFinance yf = new YFinance(restClient);
        log.debug("Create the ticker object");
        Ticker ticker = yf.ticker(tickerHistory.getTicker());
        ChartRequest.Builder builder = ChartRequest.builder()
                .start(commandUtils.getStartDate(tickerHistory))
                .end(commandUtils.getEndDate(tickerHistory))
                .interval(tickerHistory.getInterval() != null? tickerHistory.getInterval(): Interval.ONE_DAY);
        ChartRequest request = builder.build();
        log.debug("Download the history of the ticker {}", tickerHistory.getTicker());
        List<HistoryRecord> range = ticker.history(request);
        List<TickerRecord> tickerHistories = recordConverter.getTickerHistory(range);
        try (Writer writer = new FileWriter(destFile)) {
            StatefulBeanToCsv<TickerRecord> beanToCsv = new StatefulBeanToCsvBuilder<TickerRecord>(writer)
                    .build();

            beanToCsv.write(tickerHistories); // Writes the full list automatically
            log.debug("The history has been downloaded and saved to the CSV file at {}!", destFile.getPath());
        }
    }
}
