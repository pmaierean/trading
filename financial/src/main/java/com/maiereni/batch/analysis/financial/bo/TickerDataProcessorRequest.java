package com.maiereni.batch.analysis.financial.bo;

import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author pmaierean on 2026-10-03
 *
 **/
@Data
public class TickerDataProcessorRequest {
    private SecurityMaster ticker;
    private String backupFolder;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
}
