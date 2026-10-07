package com.maiereni.batch.analysis.financial.bo;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Data
public class HistogramRecord {
    @CsvBindByName(column = "Timestamp")
    private Instant timestamp;
    @CsvBindByName(column = "value")
    private BigDecimal value;
}
