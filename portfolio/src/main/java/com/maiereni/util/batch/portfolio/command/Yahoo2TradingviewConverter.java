package com.maiereni.util.batch.portfolio.command;

import com.maiereni.util.batch.portfolio.bean.TradingviewRecord;
import com.maiereni.util.batch.portfolio.bean.Yahoo2TradingviewRequest;
import com.maiereni.util.batch.portfolio.bean.YahooPortfolioRecord;
import com.maiereni.util.batch.portfolio.util.*;
import com.opencsv.bean.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts the portfolio format from Yahoo to Tradingview
 * @author pmaierean on 2026-09-26
 *
 **/
@Component
@Slf4j
public class Yahoo2TradingviewConverter implements CommandLineRunner  {

    public void convert(String input, String output) throws Exception {
        File yahoo = new File(input);
        if (yahoo.exists()) {
            saveRecorded(convert(getYahooPortfolioRecords(yahoo)), output);
        }
        else {
            throw new Exception("The input file " + input + " cannot be found");
        }
    }

    private void saveRecorded(List<TradingviewRecord> records, String output) throws Exception {
        File destination = new File(output);
        try (Writer writer = new FileWriter(destination);
            StringWriter stringWriter = new StringWriter();) {
            ColumnPositionMappingStrategy<TradingviewRecord> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(TradingviewRecord.class);

            StatefulBeanToCsv<TradingviewRecord> beanToCsv = new StatefulBeanToCsvBuilder<TradingviewRecord>(stringWriter)
                    .withMappingStrategy(columnPositionMappingStrategy)
                    .build();

            beanToCsv.write(records); // Writes the full list automatically
            writer.write("Symbol,Side,Qty,Fill Price,Commission,Closing Time\r\n");
            writer.write(stringWriter.toString());
            log.debug("The trading view file was saved at {}!", destination.getPath());
        }
    }

    private List<TradingviewRecord> convert(List<YahooPortfolioRecord> records) {
        SymbolConverter symbolConverter = new SymbolConverter();
        TransactionTypeConverter transactionTypeConverter = new TransactionTypeConverter();
        List<TradingviewRecord> list = new ArrayList<>();
        for (YahooPortfolioRecord record : records) {
            TradingviewRecord tRecord = new TradingviewRecord();
            tRecord.setSymbol(symbolConverter.toTradingview(record.getSymbol()));
            tRecord.setQuantity(record.getQuantity());
            tRecord.setClosingTime(record.getDate().replaceAll("/", "-"));
            tRecord.setSide(transactionTypeConverter.toTradingview(record.getTransactionType()));
            tRecord.setFillPrice(record.getPurchasePrice());
            tRecord.setCommission(record.getCommission());
            if (StringUtils.isNoneBlank(tRecord.getSide())) {
                list.add(tRecord);
            }
        }
        log.debug("Convert Yahoo records to Trading View records");
        return list;
    }

    private List<YahooPortfolioRecord> getYahooPortfolioRecords(File yahoo) throws Exception  {
        try (Reader reader = new FileReader(yahoo)) {
            log.debug("Reading Yahoo Portfolio Records from {}", yahoo.getAbsolutePath());
            CsvToBean<YahooPortfolioRecord> csvToBean = new CsvToBeanBuilder<YahooPortfolioRecord>(reader)
                    .withType(YahooPortfolioRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        ArgumentConverter argumentConverter = new ArgumentConverter();
        Yahoo2TradingviewRequest request = argumentConverter.convert(Yahoo2TradingviewRequest.class, args);
        if (request != null && StringUtils.isNoneBlank(request.getOutput(), request.getYahooPortfolio())) {
            log.debug("Convert portfolio");
            convert(request.getYahooPortfolio(), request.getOutput());
        }
    }
}
