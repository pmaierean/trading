package com.maiereni.util.batch.portfolio.util;

import com.maiereni.util.batch.portfolio.bean.TradingviewRecord;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
public class SymbolConverter {
    public String toTradingview(String yahooSymbol) {
        if (yahooSymbol != null) {
            if (yahooSymbol.endsWith(".TO")) {
                return "TSX:" + yahooSymbol.substring(0, yahooSymbol.length() - 3);
            }
            else {
                return "NASDAQ:" + yahooSymbol;
            }
        }
        return "";
    }
}
