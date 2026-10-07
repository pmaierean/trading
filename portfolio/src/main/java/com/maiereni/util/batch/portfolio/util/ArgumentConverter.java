package com.maiereni.util.batch.portfolio.util;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class
 * @author pmaierean on 2026-09-26
 *
 **/
public class ArgumentConverter {

    public <T> T convert(Class<T> clazz, String... args)
            throws Exception {
        Map<String, String[]> properties = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                // Split into exactly 2 parts: the key (with --) and the value
                String[] parts = arg.split("=", 2);

                // Strip the leading "--" from the key
                String key = parts[0].substring(2);
                String value = parts[1];

                // BeanUtils.populate requires a String[] value
                properties.put(key, new String[]{value});
            }
        }
        if (properties.isEmpty()) {
            return null;
        }
        T ret = clazz.getDeclaredConstructor().newInstance();
        BeanUtils.populate(ret, properties);
        return ret;
    }

}
