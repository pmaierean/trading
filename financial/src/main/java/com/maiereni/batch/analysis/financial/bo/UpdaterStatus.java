package com.maiereni.batch.analysis.financial.bo;

import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import lombok.Data;

import java.util.List;

/**
 *
 * @author pmaierean on 2026-10-03
 *
 **/
@Data
public class UpdaterStatus {
    private List<SecurityMaster> tickers;
    private int index = 0;
    private int successiveFailures;
}
