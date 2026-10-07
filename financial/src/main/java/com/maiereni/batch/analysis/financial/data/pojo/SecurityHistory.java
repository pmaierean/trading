package com.maiereni.batch.analysis.financial.data.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author pmaierean on 2026-10-02
 *
 **/
@Entity
@Table(name = "security_history")
@NamedQueries({
        @NamedQuery( name = SecurityHistory.SELECT_ALL_HISTORY, query = "SELECT sh FROM SecurityHistory sh WHERE sh.securityMaster.id = :" + SecurityHistory.SECURITY_MASTER_ID + " order by sh.timestamp desc" )
})
@Getter
@Setter
public class SecurityHistory extends BasicEntity {
    public static final String SELECT_ALL_HISTORY = "SecurityHistory.SelectAllHistory";
    public static final String SECURITY_MASTER_ID = "SecurityMasterId";
    @Id
    @UuidGenerator
    @Column(name="id")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="security_master_id")
    private SecurityMaster securityMaster;
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    @Column(name="adj_close")
    private BigDecimal adjClose;
    @Column(name="close")
    private BigDecimal close;
    @Column(name="dividends")
    private BigDecimal dividends;
    @Column(name="high")
    private BigDecimal high;
    @Column(name="low")
    private BigDecimal low;
    @Column(name="open")
    private BigDecimal open;
    @Column(name="stock_split")
    private BigDecimal stockSplit;
    @Column(name="volume")
    private BigDecimal volume;
}
