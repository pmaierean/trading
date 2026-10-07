package com.maiereni.batch.analysis.financial.command;

import com.maiereni.batch.analysis.financial.bo.Argument;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Slf4j
public abstract class BaseCommand implements CommandLineRunner {

    protected abstract void process(Map<String, String[]> properties) throws Exception;

    @Override
    public void run(String... args) throws Exception {
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
        if (!properties.isEmpty()) {
            process(properties);
        } else {
            log.error("No properties provided");
            throw new IllegalArgumentException("Missing required arguments");
        }
    }
}
