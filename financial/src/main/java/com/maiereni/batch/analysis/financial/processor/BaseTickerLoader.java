package com.maiereni.batch.analysis.financial.processor;

import com.maiereni.batch.analysis.financial.bo.TickerRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

/**
 *
 * @author pmaierean on 2026-09-24
 *
 **/
@Slf4j
public abstract class BaseTickerLoader {
    /**
     * Loads a ticker record from a CSVFile
     * @param csvFile the csv file
     * @return the loaded records
     * @throws Exception failed to load
     */
    public List<TickerRecord> loadTickerRecords(File csvFile) throws Exception {
        log.debug("Load records from file {}", csvFile.getPath());
        try (Reader reader = new FileReader(csvFile)) {
            CsvToBean<TickerRecord> csvToBean = new CsvToBeanBuilder<TickerRecord>(reader)
                    .withType(TickerRecord.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }
}
