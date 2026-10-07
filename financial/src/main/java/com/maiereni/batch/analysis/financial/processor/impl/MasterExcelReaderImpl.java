package com.maiereni.batch.analysis.financial.processor.impl;

import com.maiereni.batch.analysis.financial.bo.SecurityMaster;
import com.maiereni.batch.analysis.financial.bo.config.ExcelSources;
import com.maiereni.batch.analysis.financial.processor.MasterExcelReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of the Master Reader processor
 * @author pmaierean on 2026-09-30
 *
 **/
@Component
@Slf4j
public class MasterExcelReaderImpl implements MasterExcelReader {
    private final ExcelSources excelSources;

    public MasterExcelReaderImpl(ExcelSources excelSources) {
        this.excelSources = excelSources;
    }

    /**
     * Get the list of securities
     * @param exchange the exchange
     * @return a list of securities
     */
    @Override
    public List<SecurityMaster> getSecurityMasters(String exchange) {
        List<SecurityMaster> securityMasters = null;
        if (StringUtils.isBlank(exchange) || exchange.equalsIgnoreCase("tsx")) {
            securityMasters = readFromTSX(0);
            List<SecurityMaster> tsxv = readFromTSX(1);
            securityMasters.addAll(tsxv);
        }
        return securityMasters;
    }

    private List<SecurityMaster> readFromTSX(int sheetNo) {
        log.debug("Load securities from TSX sheet {}", sheetNo);
        List<SecurityMaster> securityMasters = new ArrayList<>();
        int ix = sheetNo == 0? 1: 2;
        try (FileInputStream fis = new FileInputStream(new File(excelSources.getTsxMaster()));
            Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(sheetNo);
            log.debug("Sheet {}", sheet.getSheetName());
            for (Row row : sheet) {
                Cell cell = row.getCell(ix);
                if (cell != null &&
                    cell.getCellType() == CellType.STRING) {
                    String exchange = cell.getStringCellValue();
                    if (exchange.equalsIgnoreCase("TSX") || exchange.equalsIgnoreCase("TSXV")) {
                        SecurityMaster securityMaster = readFromTSX(row, ix);
                        securityMaster.setExchange(exchange);
                        securityMasters.add(securityMaster);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to read data", e);
            throw new RuntimeException(e);
        }
        log.debug("Loaded a list of {} securities", securityMasters.size());
        return securityMasters;
    }

    SecurityMaster readFromTSX(Row row, int ix) {
        SecurityMaster securityMaster = new SecurityMaster();
        securityMaster.setName(getCellValue(row.getCell(ix + 1)));
        securityMaster.setTicker(getCellValue(row.getCell(ix + 2)));
        securityMaster.setIndustry(getCellValue(row.getCell(ix + 7)));
        securityMaster.setSubIndustry(getCellValue(row.getCell(ix + 8)));
        securityMaster.setType(getCellValue(row.getCell(ix + 27)));
        securityMaster.setType(getCellValue(row.getCell(ix + 28)));
        securityMaster.setMarketCap(getCellValueAsDecimal(row.getCell(ix + 3)));
        securityMaster.setShares(getCellValueAsDecimal(row.getCell(ix + 4)));
        securityMaster.setRegion(getCellValue(row.getCell(ix + 9)));
        return securityMaster;
    }

    private BigDecimal getCellValueAsDecimal(Cell cell) {
        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }
        return null;
    }

    private String getCellValue(Cell cell) {
        if (cell != null &&
            cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        }
        return null;
    }
}
