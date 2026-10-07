package com.maiereni.batch.analysis.financial.bo;

import lombok.Data;

/**
 *
 * @author pmaierean on 2026-09-30
 *
 **/
@Data
public class MasterDataRequest {
    private String exchange;
    private String csvOutput;
    private String sqlOutput;
}
