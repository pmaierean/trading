package com.maiereni.batch.analysis.financial.util;

import com.opencsv.bean.AbstractBeanField;

import java.time.Instant;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
public class InstantConverter extends AbstractBeanField<String, Instant> {
    @Override
    protected Instant convert(String value) {
        return Instant.parse(value);
    }
}
