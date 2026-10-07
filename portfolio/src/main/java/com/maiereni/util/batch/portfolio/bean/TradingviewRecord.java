package com.maiereni.util.batch.portfolio.bean;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
@Data
public class TradingviewRecord {
    @CsvBindByName(column = "Symbol")
    @CsvBindByPosition(position = 0)
    private String symbol;
    @CsvBindByName(column = "Side")
    @CsvBindByPosition(position = 1)
    private String side;
    @CsvBindByName(column = "Qty")
    @CsvBindByPosition(position = 2)
    private BigDecimal quantity;
    @CsvBindByName(column = "Fill Price")
    @CsvBindByPosition(position = 3)
    private BigDecimal fillPrice;
    @CsvBindByName(column = "Commission")
    @CsvBindByPosition(position = 4)
    private BigDecimal commission;
    @CsvBindByName(column = "Closing Time")
    @CsvBindByPosition(position = 5)
    private String closingTime;
}
