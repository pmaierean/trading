package com.maiereni.batch.analysis.financial.data.dao.impl;

import com.maiereni.batch.analysis.financial.data.dao.StocksDao;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityHistory;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
@Repository
@Slf4j
public class StocksDaoImpl implements StocksDao {
    private final EntityManager entityManager;

    public StocksDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Get all tickers
     * @return a list of tickers
     */
    @Transactional
    @Override
    public List<SecurityMaster> getTickers() {
        TypedQuery<SecurityMaster> q = entityManager.createNamedQuery(SecurityMaster.FIND_ALL_ACTIVE_TICKERS, SecurityMaster.class);
        return q.getResultList();
    }

    /**
     * Get the most recent timestap of a ticker
     * @param securityMaster the security master
     * @return a timestamp
     */
    @Transactional
    @Override
    public LocalDateTime getMostRecentDate(SecurityMaster securityMaster) {
        TypedQuery<SecurityHistory> q = entityManager.createNamedQuery(SecurityHistory.SELECT_ALL_HISTORY, SecurityHistory.class)
                .setMaxResults(1);
        q.setParameter(SecurityHistory.SECURITY_MASTER_ID, securityMaster.getId());
        Optional<SecurityHistory> securityHistory = q.getResultList().stream().findFirst();
        return securityHistory.map(SecurityHistory::getTimestamp).orElse(null);
    }

    /**
     * Save securities histories
     * @param securityHistories a list of security histories to save
     */
    @Transactional
    @Override
    public void save(List<SecurityHistory> securityHistories) {
        if (securityHistories != null) {
            log.debug("Saving security histories");
            for(SecurityHistory securityHistory : securityHistories) {
                entityManager.persist(securityHistory);
            }
            entityManager.flush();
        }
    }
}
