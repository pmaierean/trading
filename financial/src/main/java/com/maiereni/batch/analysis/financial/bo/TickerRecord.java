package com.maiereni.batch.analysis.financial.bo;

import com.maiereni.batch.analysis.financial.util.InstantConverter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Data
public class TickerRecord {
    @CsvBindByName(column = "Timestamp")
    @CsvCustomBindByPosition(position = 7, converter = InstantConverter.class)
    private Instant timestamp;
    @CsvBindByName(column = "Open")
    @CsvBindByPosition(position = 5)
    private BigDecimal open;
    @CsvBindByName(column = "High")
    @CsvBindByPosition(position = 3)
    private BigDecimal high;
    @CsvBindByName(column = "Low")
    @CsvBindByPosition(position = 4)
    private BigDecimal low;
    @CsvBindByName(column = "Close")
    @CsvBindByPosition(position = 1)
    private BigDecimal close;
    @CsvBindByName(column = "Adj Close")
    @CsvBindByPosition(position = 0)
    private BigDecimal adjClose;
    @CsvBindByName(column = "Volume")
    @CsvBindByPosition(position = 8)
    private long volume;
    @CsvBindByName(column = "Dividends")
    @CsvBindByPosition(position = 2)
    private BigDecimal dividends;
    @CsvBindByName(column = "Stock Split")
    @CsvBindByPosition(position = 6)
    private BigDecimal stockSplits;
}
