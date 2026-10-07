package com.maiereni.util.batch.portfolio.bean;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
@Data
public class YahooPortfolioRecord {
    @CsvBindByName(column = "Symbol")
    private String symbol; 
    @CsvBindByName(column = "Current Price")
    private BigDecimal crtPrice;
    @CsvBindByName(column = "Date")
    private String date; 
    @CsvBindByName(column = "Time")
    private String time; 
    @CsvBindByName(column = "Change")
    private BigDecimal change;
    @CsvBindByName(column = "Open")
    private BigDecimal open;
    @CsvBindByName(column = "High")
    private BigDecimal high;
    @CsvBindByName(column = "Low")
    private BigDecimal low;
    @CsvBindByName(column = "Volume")
    private BigDecimal volume;
    @CsvBindByName(column = "Trade Date")
    private String tradeDate; 
    @CsvBindByName(column = "Purchase Price")
    private BigDecimal purchasePrice;
    @CsvBindByName(column = "Quantity")
    private BigDecimal quantity;
    @CsvBindByName(column = "Commission")
    private BigDecimal commission;
    @CsvBindByName(column = "High Limit")
    private String highLimit; 
    @CsvBindByName(column = "Low Limit")
    private String lowLimit; 
    @CsvBindByName(column = "Comment")
    private String comment; 
    @CsvBindByName(column = "Transaction Type")
    private String transactionType; 
}
