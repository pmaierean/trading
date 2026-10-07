package com.maiereni.util.batch.portfolio.util;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
public class TradingViewHeaderColumnNameStrategy<T> extends HeaderColumnNameMappingStrategy<T>  {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        return new String[] {"Symbol","Side","Qty","Fill Price","Commission","Closing Time"};
    }
}
