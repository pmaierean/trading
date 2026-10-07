package com.maiereni.batch.analysis.financial.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Getter
@Setter
public class MacdRequest extends TickerRequest{
    private int macdPeriod = 12;
    private int fast = 12;
    private int slow = 26;
    private int signalPeriod = 26;
    private String resultFilePattern = "${destinationDir}/${ticker}-${startDate}-${endDate}-${interval}-${type}.csv";
}
