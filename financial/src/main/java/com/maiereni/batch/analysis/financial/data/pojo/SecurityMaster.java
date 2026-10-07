package com.maiereni.batch.analysis.financial.data.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
@Entity
@Table(name = "security_master")
@NamedQueries({
        @NamedQuery(name = SecurityMaster.FIND_ALL_ACTIVE_TICKERS, query = "SELECT sm FROM SecurityMaster sm WHERE sm.status = 1 ORDER BY sm.name"),
})
@Getter
@Setter
public class SecurityMaster extends BasicEntity {
    public static final String FIND_ALL_ACTIVE_TICKERS = "SecurityMaster.findAllActiveTickers";
    @Id
    @UuidGenerator
    @Column(name="id")
    private UUID id;
    @Column(name="name")
    private String name;
    @Column(name="ticker")
    private String ticker;
    @Column(name="exchange")
    private String exchange;
    @Column(name="security_type")
    private String securityType;
    @Column(name="region")
    private String region;
    @Column(name="industry")
    private String industry;
    @Column(name="sub_industry")
    private String subIndustry;
    @Column(name="market_cap")
    private BigDecimal marketCapitalization;
    @Column(name="shares_number")
    private BigDecimal sharesNumber;
    @OneToMany(fetch=FetchType.LAZY, mappedBy = "securityMaster")
    private Set<SecurityHistory> securityHistories;
}
