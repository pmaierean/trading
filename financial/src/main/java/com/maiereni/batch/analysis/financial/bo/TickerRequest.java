package com.maiereni.batch.analysis.financial.bo;

import am.ik.yfinance4j.Interval;
import am.ik.yfinance4j.Period;
import lombok.Getter;
import lombok.Setter;

/**
 * The class of arguments taken by the ticker history
 * @author pmaierean on 2026-09-24
 *
 **/
@Setter
@Getter
public class TickerRequest extends Argument {
    private String ticker;
    private String startDate;
    private String endDate;
    private Interval interval;
    private Period period;
    private String sourceFilePattern = "${sourceDir}/${ticker}-${startDate}-${endDate}-${interval}.csv";
}
