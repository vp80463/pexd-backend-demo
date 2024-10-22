package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="sp_customer_dw")
@Setter
@Getter
public class SpCustomerDw {

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="account_month", length=6)
    private String accountMonth;

    @Column(name="site_id", length=10)
    private String siteId;

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

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="so_line", precision=10)
    private Integer soLine = CommonConstants.INTEGER_ZERO;

    @Column(name="allocated_line", precision=10)
    private Integer allocatedLine = CommonConstants.INTEGER_ZERO;

    @Column(name="bo_line", precision=10)
    private Integer boLine = CommonConstants.INTEGER_ZERO;

    @Column(name="so_cancel_line", precision=10)
    private Integer soCancelLine = CommonConstants.INTEGER_ZERO;

    @Column(name="shipment_line", precision=10)
    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    @Column(name="return_line", precision=10)
    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    @Column(name="so_amt", precision=18, scale=2)
    private BigDecimal soAmt = BigDecimal.ZERO;

    @Column(name="allocated_amt", precision=18, scale=2)
    private BigDecimal allocatedAmt = BigDecimal.ZERO;

    @Column(name="bo_amt", precision=18, scale=2)
    private BigDecimal boAmt = BigDecimal.ZERO;

    @Column(name="so_cancel_amt", precision=18, scale=2)
    private BigDecimal soCancelAmt = BigDecimal.ZERO;

    @Column(name="invoice_amt", precision=18, scale=2)
    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    @Column(name="invoice_cost", precision=18, scale=4)
    private BigDecimal invoiceCost = BigDecimal.ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
