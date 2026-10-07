package com.maiereni.batch.analysis.financial.bo;

import lombok.Data;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
@Data
public class BatchUpdaterRequest {
    private long delay=20L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private String tickers;
    private String backupFolder;
    private long days = 20L;
    private int maxSuccessiveFailures = 3;
}
