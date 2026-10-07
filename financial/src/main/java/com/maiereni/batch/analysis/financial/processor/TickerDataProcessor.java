package com.maiereni.batch.analysis.financial.processor;

import com.maiereni.batch.analysis.financial.bo.TickerDataProcessorRequest;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The processor to download data for a ticker
 * @author pmaierean on 2026-10-02
 *
 **/
public interface TickerDataProcessor {
    List<SecurityMaster> getTickers(String filter);
    void downloadTickerData(TickerDataProcessorRequest request) throws Exception;
}
