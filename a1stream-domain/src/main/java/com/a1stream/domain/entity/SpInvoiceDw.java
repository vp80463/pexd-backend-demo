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
@Table(name="sp_invoice_dw")
@Setter
@Getter
public class SpInvoiceDw {

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

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="invoice_no", length=20)
    private String invoiceNo;

    @Column(name="invoice_type", length=40)
    private String invoiceType;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=256)
    private String productNm;

    @Column(name="middle_group_cd", length=20)
    private String middleGroupCd;

    @Column(name="middle_group_nm", length=200)
    private String middleGroupNm;

    @Column(name="large_group_cd", length=20)
    private String largeGroupCd;

    @Column(name="large_group_nm", length=200)
    private String largeGroupNm;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="abc_type", length=10)
    private String abcType;

    @Column(name="invoice_qty", precision=18, scale=2)
    private BigDecimal invoiceQty = BigDecimal.ZERO;

    @Column(name="invoice_price", precision=18, scale=2)
    private BigDecimal invoicePrice = BigDecimal.ZERO;

    @Column(name="invoice_amt", precision=18, scale=2)
    private BigDecimal invoiceAmt = BigDecimal.ZERO;

    @Column(name="invoice_cost", precision=18, scale=4)
    private BigDecimal invoiceCost = BigDecimal.ZERO;

    @Column(name="shipment_line", precision=10)
    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    @Column(name="return_line", precision=10)
    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    @Column(name="sales_order_no", length=20)
    private String salesOrderNo;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="order_to_type", length=40)
    private String orderToType;

    @Column(name="create_datetime", length=14)
    private String createDatetime;

    @Column(name="invoice_price_not_vat", precision=18, scale=2)
    private BigDecimal invoicePriceNotVat = BigDecimal.ZERO;

    @Column(name="invoice_amt_not_vat", precision=18, scale=2)
    private BigDecimal invoiceAmtNotVat = BigDecimal.ZERO;
}
