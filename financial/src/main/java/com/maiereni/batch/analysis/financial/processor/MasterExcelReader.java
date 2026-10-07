package com.maiereni.batch.analysis.financial.processor;

import com.maiereni.batch.analysis.financial.bo.SecurityMaster;

import java.util.List;

/**
 *
 * @author pmaierean on 2026-09-30
 *
 **/
public interface MasterExcelReader {
    List<SecurityMaster> getSecurityMasters(String exchange);
}
