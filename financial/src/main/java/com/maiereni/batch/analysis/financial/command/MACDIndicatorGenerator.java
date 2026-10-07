package com.maiereni.batch.analysis.financial.command;

import com.maiereni.batch.analysis.financial.bo.HistogramRecord;
import com.maiereni.batch.analysis.financial.bo.MacdRequest;
import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import com.maiereni.batch.analysis.financial.processor.MACDIndicatorLoader;
import com.maiereni.batch.analysis.financial.util.CommandUtils;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.num.Num;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A command class that
 * @author pmaierean on 2026-09-24
 *
 **/
@Component
@Order(2)
@Slf4j
public class MACDIndicatorGenerator extends BaseCommand {
    private final CommandUtils commandUtils = new CommandUtils();
    private final MACDIndicatorLoader loader;

    public MACDIndicatorGenerator(@Autowired MACDIndicatorLoader loader) {
        this.loader = loader;
    }

    @Override
    protected void process(Map<String, String[]> properties) throws Exception {
        MacdRequest macdRequest = new MacdRequest();
        BeanUtils.populate(macdRequest, properties);
        if (StringUtils.isNotBlank(macdRequest.getTicker())) {
            MACDIndicator macd = loader.build(macdRequest);
            EMAIndicator signal = new EMAIndicator(macd, macdRequest.getSignalPeriod());
            BarSeries series =  macd.getBarSeries();
            int count = series.getBarCount();
            List<HistogramRecord> recordList =  new ArrayList<>();
            for (int i = 0; i < count; i++) {
                Num hist = macd.getValue(i).minus(signal.getValue(i));
                HistogramRecord record = new HistogramRecord();
                record.setTimestamp(series.getBar(i).getBeginTime());
                record.setValue(hist.bigDecimalValue());
                recordList.add(record);
            }
            File destFile = commandUtils.getMacdHistogramCSVFile(macdRequest);
            try (Writer writer = new FileWriter(destFile)) {
                StatefulBeanToCsv<HistogramRecord> beanToCsv = new StatefulBeanToCsvBuilder<HistogramRecord>(writer)
                        .build();

                beanToCsv.write(recordList); // Writes the full list automatically
                log.debug("The history has been downloaded and saved to the CSV file at {}!", destFile.getPath());
            }
        }
    }
}
