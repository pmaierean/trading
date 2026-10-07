package com.maiereni.batch.analysis.financial.processor;

import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.indicators.averages.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.io.File;
import java.math.MathContext;
import java.time.Duration;
import java.util.List;

/**
 * Loads an Ema Indicator from a CSV file containing ticker data
 * @author pmaierean on 2026-09-24
 *
 **/
@Slf4j
public class IndicatorLoader extends BaseTickerLoader{
    /**
     * Load an close price EMAIndicator
     * @param tickerName ticker name
     * @param csvFile the trading history
     * @param period in days
     * @return an EMA indicator
     * @throws Exception failure to load
     */
    public EMAIndicator loadOnClosePrice(String tickerName, File csvFile, int period)  throws Exception {
        List<TickerRecord> records = loadTickerRecords(csvFile);
        BarSeries series = new BaseBarSeriesBuilder().withName(tickerName).build();
        for(TickerRecord record : records){
            Bar bar = new BaseBar(
                   null,// Duration
                    record.getTimestamp(), // beginTime
                    record.getTimestamp(), // endTime
                    DecimalNum.valueOf(record.getOpen(), MathContext.DECIMAL64), // openPrice
                    DecimalNum.valueOf(record.getHigh(), MathContext.DECIMAL64), // highPrice
                    DecimalNum.valueOf(record.getLow(), MathContext.DECIMAL64), // lowPrice
                    DecimalNum.valueOf(record.getClose(), MathContext.DECIMAL64), // closePrice
                    DecimalNum.valueOf(record.getVolume(), MathContext.DECIMAL64),// volume
                    DecimalNum.valueOf(0), // amount
                    0 // trades
            );
            series.addBar(bar);
        }
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        return new EMAIndicator(closePrice, period);
    }
}
