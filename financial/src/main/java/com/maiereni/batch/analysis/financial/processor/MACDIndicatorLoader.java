package com.maiereni.batch.analysis.financial.processor;

import com.maiereni.batch.analysis.financial.bo.MacdRequest;
import com.maiereni.batch.analysis.financial.util.CommandUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.averages.EMAIndicator;

import java.io.File;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Component
@Slf4j
public class MACDIndicatorLoader extends IndicatorLoader {
    private final CommandUtils commandUtils = new CommandUtils();

    /**
     * Load the MACDIndicator for the request
     * @param request the request
     * @return an indicator
     * @throws Exception failure
     */
    public MACDIndicator build(MacdRequest request) throws Exception {
        File csvFile = commandUtils.getDownloadedCSVFile(request);
        if (csvFile == null || !csvFile.exists()) {
            throw new Exception("CSV file not found");
        }
        EMAIndicator series = loadOnClosePrice(request.getTicker(), csvFile, request.getMacdPeriod());
        return new MACDIndicator(series, request.getFast(), request.getSlow());
    }
}
