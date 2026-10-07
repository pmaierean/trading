package com.maiereni.batch.analysis.financial.bo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author pmaierean on 2026-09-30
 *
 **/
@Configuration
@ConfigurationProperties(prefix = "app.source")
@Data
public class ExcelSources {
    private String tsxMaster;
}
