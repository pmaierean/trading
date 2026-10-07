package com.maiereni.batch.analysis.financial.command;

import com.maiereni.batch.analysis.financial.bo.BatchUpdaterRequest;
import com.maiereni.batch.analysis.financial.bo.TickerDataProcessorRequest;
import com.maiereni.batch.analysis.financial.bo.UpdaterStatus;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import com.maiereni.batch.analysis.financial.processor.TickerDataProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * A batch process that downloads stock history for a large number of stocks with delays and updates
 * files
 *
 * @author pmaierean on 2026-10-01
 *
 **/
@Order(4)
@Component
@Slf4j
public class BatchUpdater extends BaseCommand {
    private final TickerDataProcessor tickerDataProcessor;
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public BatchUpdater(TickerDataProcessor tickerDataProcessor) {
        this.tickerDataProcessor = tickerDataProcessor;
    }

    @Override
    protected void process(Map<String, String[]> properties) throws Exception {
        final BatchUpdaterRequest request = new BatchUpdaterRequest();
        BeanUtils.populate(request, properties);
        if (StringUtils.isNotBlank(request.getBackupFolder())) {
            final TickerDataProcessorRequest processorRequest = new TickerDataProcessorRequest();
            processorRequest.setEndDate(LocalDateTime.now().minusDays(1L).with(LocalTime.MAX));
            if (request.getDays() > 0) {
                processorRequest.setStartDate(LocalDateTime.now().minusDays(request.getDays() + 1L).with(LocalTime.MIN));
            }
            processorRequest.setBackupFolder(request.getBackupFolder());
            final UpdaterStatus status = new UpdaterStatus();
            status.setTickers(tickerDataProcessor.getTickers(request.getTickers()));
            scheduler.scheduleWithFixedDelay(() -> {
                log.debug("Process step: {}", status.getIndex());
                if (status.getIndex() >= status.getTickers().size()) {
                    log.info("Done processing");
                    shutdownAndExit();
                }
                else {
                    try {
                        SecurityMaster currentTicker = status.getTickers().get(status.getIndex());
                        processorRequest.setTicker(currentTicker);
                        tickerDataProcessor.downloadTickerData(processorRequest);
                        log.info("Done processing step: {}", status.getIndex());
                        status.setIndex(status.getIndex() + 1);
                        if (status.getIndex() >= status.getTickers().size()) {
                            log.info("Done processing");
                            shutdownAndExit();
                        }
                    }
                    catch (Exception e) {
                        log.error("Error while downloading ticker data", e);
                        if (status.getSuccessiveFailures() > request.getMaxSuccessiveFailures()) {
                            shutdownAndExit();
                        }
                        status.setSuccessiveFailures(status.getSuccessiveFailures() + 1);
                    }
                }
            },
            0,
            request.getDelay(),
            request.getTimeUnit());
        }
    }

    private void shutdownAndExit() {
        scheduler.shutdown();
        log.debug("Finish downloading");
        // allow Spring Boot to exit
        System.exit(0);
    }

}
