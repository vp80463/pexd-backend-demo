package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="cmm_mst_acct_month")
@Setter
@Getter
public class CmmMstAcctMonth extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="acct_month_id", unique=true, nullable=false)
    private Long acctMonthId;

    @Column(name="account_month", nullable=false, length=6)
    private String accountMonth;

    @Column(name="from_date", nullable=false, length=8)
    private String fromDate;

    @Column(name="to_date", nullable=false, length=8)
    private String toDate;


}
