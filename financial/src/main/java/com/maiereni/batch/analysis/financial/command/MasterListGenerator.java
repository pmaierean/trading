package com.maiereni.batch.analysis.financial.command;

import com.maiereni.batch.analysis.financial.bo.MasterDataRequest;
import com.maiereni.batch.analysis.financial.bo.SecurityMaster;
import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import com.maiereni.batch.analysis.financial.processor.MasterExcelReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author pmaierean on 2026-09-30
 *
 **/
@Component
@Order(3)
@Slf4j
public class MasterListGenerator extends BaseCommand {
    private final MasterExcelReader masterReader;
    public MasterListGenerator(MasterExcelReader masterReader) {
        this.masterReader = masterReader;
    }

    @Override
    protected void process(Map<String, String[]> properties) throws Exception {
        MasterDataRequest masterDataRequest = new MasterDataRequest();
        BeanUtils.populate(masterDataRequest, properties);
        if (StringUtils.isNotBlank(masterDataRequest.getExchange()) && (
            StringUtils.isNotBlank(masterDataRequest.getCsvOutput()) ||
            StringUtils.isNotBlank(masterDataRequest.getSqlOutput()))) {
            List<SecurityMaster> securityMasters = masterReader.getSecurityMasters(masterDataRequest.getExchange());
            if (StringUtils.isNotBlank(masterDataRequest.getCsvOutput())) {
                saveCSV(securityMasters, masterDataRequest.getCsvOutput());
            }
            if (StringUtils.isNotBlank(masterDataRequest.getSqlOutput())) {
                saveSQL(securityMasters, masterDataRequest.getSqlOutput());
            }
        }
    }

    private void saveCSV(List<SecurityMaster> securityMasters, String output) throws Exception {
        try (FileWriter writer = new FileWriter(new File(output));
             StringWriter stringWriter = new StringWriter();) {
            ColumnPositionMappingStrategy<SecurityMaster> columnPositionMappingStrategy = new ColumnPositionMappingStrategy<>();
            columnPositionMappingStrategy.setType(SecurityMaster.class);

            StatefulBeanToCsv<SecurityMaster> beanToCsv = new StatefulBeanToCsvBuilder<SecurityMaster>(stringWriter)
                    .withMappingStrategy(columnPositionMappingStrategy)
                    .build();

            beanToCsv.write(securityMasters);
            writer.write("Name,Ticker,Industry,Subindustry,Exchange,Type,Subtype,Market Cap,Shares,Region\r\n");
            writer.write(stringWriter.toString());
            log.debug("The master was saved to CSV file at {}!", output);
        }
    }
    private static final String TEMPLATE_INSERT_MASTER = "INSERT INTO security_master (ID, NAME, TICKER, EXCHANGE, SECURITY_TYPE, SECURITY_SUB_TYPE, REGION, INDUSTRY, SUB_INDUSTRY, MARKET_CAP, SHARES_NUMBER, STATUS, CREATION_DATE, CREATED_BY) VALUES\r\n" +
            "('${id}','${name}','${ticker}','${exchange}','${type}','${sub_type}','${region}', '${industry}','${subIndustry}', ${marketCap}, ${shareNumber}, 1, '${creationDate}', '${createdBy}');\r\n";
    private void saveSQL(List<SecurityMaster> securityMasters, String output) throws Exception{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new Date());
        try (FileWriter writer = new FileWriter(new File(output));
             StringWriter stringWriter = new StringWriter();) {
            for(SecurityMaster securityMaster: securityMasters){
                String sql = getStatement(securityMaster, date);
                stringWriter.append(sql);
            }
            writer.write(stringWriter.toString());
            log.debug("The master was saved to SQL file at {}!", output);
        }
    }

    private String getStatement(SecurityMaster securityMaster, String date) {
        DecimalFormat df = new DecimalFormat("####.00");
        Map<String, String> valuesMap = new HashMap<>();
        valuesMap.put("id", UUID.randomUUID().toString());
        valuesMap.put("name", securityMaster.getName().replace("'", "''"));
        valuesMap.put("ticker", securityMaster.getTicker());
        valuesMap.put("exchange", securityMaster.getExchange());
        valuesMap.put("type", securityMaster.getType() == null ? "" : securityMaster.getType());
        valuesMap.put("sub_type", securityMaster.getSubType() == null ? "" : securityMaster.getSubType());
        valuesMap.put("region", securityMaster.getRegion());
        valuesMap.put("industry", securityMaster.getIndustry());
        valuesMap.put("subIndustry", securityMaster.getSubIndustry() == null ? "" : securityMaster.getSubIndustry());
        valuesMap.put("marketCap", df.format(securityMaster.getMarketCap()));
        valuesMap.put("shareNumber", df.format(securityMaster.getShares()));
        valuesMap.put("creationDate", date);
        valuesMap.put("createdBy", "boot");
        StringSubstitutor sub = new StringSubstitutor(valuesMap);
        return sub.replace(TEMPLATE_INSERT_MASTER);
    }
}
