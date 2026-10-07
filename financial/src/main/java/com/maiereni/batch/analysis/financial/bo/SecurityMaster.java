package com.maiereni.batch.analysis.financial.bo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * @author pmaierean on 2026-09-30
 *
 **/
@Data
public class SecurityMaster {
    @CsvBindByName(column = "Name")
    @CsvBindByPosition(position = 0)
    private String name;
    @CsvBindByName(column = "Ticker")
    @CsvBindByPosition(position = 1)
    private String ticker;
    @CsvBindByName(column = "Industry")
    @CsvBindByPosition(position = 2)
    private String industry;
    @CsvBindByName(column = "Subindustry")
    @CsvBindByPosition(position = 3)
    private String subIndustry;
    @CsvBindByName(column = "Exchange")
    @CsvBindByPosition(position = 4)
    private String exchange;
    @CsvBindByName(column = "Type")
    @CsvBindByPosition(position = 5)
    private String type;
    @CsvBindByName(column = "Subtype")
    @CsvBindByPosition(position = 6)
    private String subType;
    @CsvBindByName(column = "Market Cap")
    @CsvBindByPosition(position = 7)
    private BigDecimal marketCap;
    @CsvBindByName(column = "Shares")
    @CsvBindByPosition(position = 8)
    private BigDecimal shares;
    @CsvBindByName(column = "Region")
    @CsvBindByPosition(position = 9)
    private String region;
}
