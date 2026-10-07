package com.maiereni.batch.analysis.financial.util;

import am.ik.yfinance4j.chart.HistoryRecord;
import com.maiereni.batch.analysis.financial.bo.TickerRecord;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
public class RecordConverter {

    public List<TickerRecord> getTickerHistory(List<HistoryRecord> histories) {
        List<TickerRecord> tickerHistory = new ArrayList<TickerRecord>();
        for (HistoryRecord history : histories) {
            tickerHistory.add(getTickerRecord(history));
        }
        return tickerHistory;
    }

    public TickerRecord getTickerRecord(HistoryRecord historyRecord) {
        TickerRecord tickerRecord = new TickerRecord();
        if (historyRecord != null) {
            tickerRecord.setAdjClose(historyRecord.adjClose());
            tickerRecord.setClose(historyRecord.close());
            tickerRecord.setDividends(historyRecord.dividends());
            tickerRecord.setOpen(historyRecord.open());
            tickerRecord.setHigh(historyRecord.high());
            tickerRecord.setLow(historyRecord.low());
            tickerRecord.setVolume(historyRecord.volume());
            tickerRecord.setStockSplits(historyRecord.stockSplits());
            tickerRecord.setTimestamp(historyRecord.timestamp());
        }
        return tickerRecord;
    }
}
