package com.maiereni.batch.analysis.financial.data.dao;

import com.maiereni.batch.analysis.financial.data.pojo.SecurityHistory;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
public interface StocksDao {
    List<SecurityMaster> getTickers();
    LocalDateTime getMostRecentDate(SecurityMaster securityMaster);
    void save(List<SecurityHistory> securityHistories);
}
