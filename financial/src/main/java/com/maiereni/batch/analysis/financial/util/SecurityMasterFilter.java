package com.maiereni.batch.analysis.financial.util;

import com.maiereni.batch.analysis.financial.bo.Ticker;
import com.maiereni.batch.analysis.financial.data.pojo.SecurityMaster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author pmaierean on 2026-10-05
 *
 **/
@Slf4j
public class SecurityMasterFilter {
    private final List<Ticker> tickers = new ArrayList<>();
    private SecurityMasterFilter() {}

    /**
     * Gets a security master
     * @return an instance
     */
    public static SecurityMasterFilter build(String filter) {
        SecurityMasterFilter ret = new SecurityMasterFilter();
        if (StringUtils.isNotBlank(filter)) {
            String[] filters = filter.split(",");
            for (String s : filters) {
                String[] split = s.split("\\x2E");
                Ticker ticker = new Ticker();
                ticker.setTickerSymbol(split[0]);
                if (split.length > 1) {
                    switch (split[1]) {
                        case "TO":
                            ticker.setExchangeName("TSX");
                            break;
                        case "V":
                            ticker.setExchangeName("TSXV");
                            break;
                    }
                }
                ret.tickers.add(ticker);
            }
        }
        return ret;
    }

    /**
     * Apply filter
     * @param securityMasters the security master
     * @return a list of security master objects
     */
    public List<SecurityMaster> filter(List<SecurityMaster> securityMasters) {
        if (!tickers.isEmpty() && securityMasters != null) {
            log.debug("Filtering security masters");
            final List<SecurityMaster> ret = new ArrayList<>();
            securityMasters.forEach(securityMaster -> {
                tickers.forEach(ticker -> {
                    String symbol = ticker.getTickerSymbol();
                    if (symbol.endsWith("-UN")) {
                        symbol = symbol.substring(0, symbol.length() - 3);
                    }
                   if (symbol.equalsIgnoreCase(securityMaster.getTicker())) {
                       if (ticker.getExchangeName() == null || ticker.getExchangeName().equals(securityMaster.getExchange())) {
                           ret.add(securityMaster);
                           ticker.setActive(true);
                           log.debug("Security master found for {}", ticker);
                       }
                   }
                });
            });
            tickers.forEach(ticker -> {
                if (!ticker.isActive()) {
                    log.error("Security master not found for {}", ticker);
                }
            });
            return ret;
        }
        return securityMasters;
    }
}
