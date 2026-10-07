package com.maiereni.util.batch.portfolio.util;

import com.maiereni.util.batch.portfolio.bean.TradingviewRecord;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
public class TransactionTypeConverter {
    public String toTradingview(String transactionType) {
        if (transactionType != null) {
            switch (transactionType) {
                case "BUY":
                    return "buy";
                case "SELL":
                    return "sell";
            }
        }
        return null;
    }
}
