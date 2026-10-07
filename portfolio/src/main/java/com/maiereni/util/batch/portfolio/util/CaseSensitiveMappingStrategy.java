package com.maiereni.util.batch.portfolio.util;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author pmaierean on 2026-09-26
 *
 **/
public class CaseSensitiveMappingStrategy <T> extends HeaderColumnNameMappingStrategy<T> {

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        // If an explicit layout order is already initialized by OpenCSV, use it
        String[] header = super.generateHeader(bean);
        if (header.length == 0) {
            return header;
        }

        // Pull the exact casing directly from the field annotations
        return Arrays.stream(this.getType().getDeclaredFields())
                .map(field -> field.getAnnotation(CsvBindByName.class))
                .filter(Objects::nonNull)
                .map(CsvBindByName::column)
                .toArray(String[]::new);
    }
}
