package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="sp_stock_account")
@Setter
@Getter
public class SpStockAccount {

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="account_month", length=6)
    private String accountMonth;

    @Column(name="target_year", length=4)
    private String targetYear;

    @Column(name="target_month", length=2)
    private String targetMonth;

    @Column(name="target_day", length=8)
    private String targetDay;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="large_group_cd", length=20)
    private String largeGroupCd;

    @Column(name="large_group_nm", length=200)
    private String largeGroupNm;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="begin_month_stock_amt", precision=18, scale=2)
    private BigDecimal beginMonthStockAmt = BigDecimal.ZERO;

    @Column(name="receipt_amt", precision=18, scale=2)
    private BigDecimal receiptAmt = BigDecimal.ZERO;

    @Column(name="sales_cost_amt", precision=18, scale=2)
    private BigDecimal salesCostAmt = BigDecimal.ZERO;

    @Column(name="return_cost_amt", precision=18, scale=2)
    private BigDecimal returnCostAmt = BigDecimal.ZERO;

    @Column(name="adjust_plus_amt", precision=18, scale=2)
    private BigDecimal adjustPlusAmt = BigDecimal.ZERO;

    @Column(name="adjust_minus_amt", precision=18, scale=2)
    private BigDecimal adjustMinusAmt = BigDecimal.ZERO;

    @Column(name="disposal_amt", precision=18, scale=2)
    private BigDecimal disposalAmt = BigDecimal.ZERO;

    @Column(name="transfer_in_amt", precision=18, scale=2)
    private BigDecimal transferInAmt = BigDecimal.ZERO;

    @Column(name="transfer_out_amt", precision=18, scale=2)
    private BigDecimal transferOutAmt = BigDecimal.ZERO;

    @Column(name="balance_cost_amt", precision=18, scale=2)
    private BigDecimal balanceCostAmt = BigDecimal.ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
