package com.maiereni.batch.analysis.financial.bo;

import lombok.Data;

/**
 *
 * @author pmaierean on 2026-10-05
 *
 **/
@Data
public class Ticker {
    private String exchangeName;
    private String tickerSymbol;
    private boolean active;

    public String toString() {
        return tickerSymbol + "." + exchangeName;
    }
}
