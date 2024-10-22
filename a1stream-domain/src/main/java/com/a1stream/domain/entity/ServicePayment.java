package com.a1stream.domain.entity;

import java.math.BigDecimal;

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
@Table(name="service_payment")
@Setter
@Getter
public class ServicePayment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="payment_id", unique=true, nullable=false)
    private Long paymentId;

    @Column(name="factory_payment_control_no", length=40)
    private String factoryPaymentControlNo;

    @Column(name="factory_budget_settle_date", length=8)
    private String factoryBudgetSettleDate;

    @Column(name="factory_doc_receipt_date", length=8)
    private String factoryDocReceiptDate;

    @Column(name="receipt_date", length=8)
    private String receiptDate;

    @Column(name="target_month", length=6)
    private String targetMonth;

    @Column(name="confirm_date", length=8)
    private String confirmDate;

    @Column(name="payment_category", length=40)
    private String paymentCategory;

    @Column(name="payment_status", length=40)
    private String paymentStatus;

    @Column(name="vat_cd", length=20)
    private String vatCd;

    @Column(name="invoice_no", length=40)
    private String invoiceNo;

    @Column(name="invoice_date", length=8)
    private String invoiceDate;

    @Column(name="serial_no", length=40)
    private String serialNo;

    @Column(name="bulletin_no", length=40)
    private String bulletinNo;

    @Column(name="payment_amt_warranty_claim_total", precision=18)
    private BigDecimal paymentAmtWarrantyClaimTotal = BigDecimal.ZERO;

    @Column(name="payment_amt_warranty_claim_job", precision=18)
    private BigDecimal paymentAmtWarrantyClaimJob = BigDecimal.ZERO;

    @Column(name="payment_amt_warranty_claim_part", precision=18)
    private BigDecimal paymentAmtWarrantyClaimPart = BigDecimal.ZERO;

    @Column(name="payment_amt_battery_warranty", precision=18)
    private BigDecimal paymentAmtBatteryWarranty = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_total", precision=18)
    private BigDecimal paymentAmtFreeCouponTotal = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_major", precision=18)
    private BigDecimal paymentAmtFreeCouponMajor = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_minor", precision=18)
    private BigDecimal paymentAmtFreeCouponMinor = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_level1", precision=18)
    private BigDecimal paymentAmtFreeCouponLevel1 = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_level2", precision=18)
    private BigDecimal paymentAmtFreeCouponLevel2 = BigDecimal.ZERO;

    @Column(name="payment_amt_free_coupon_level3", precision=18)
    private BigDecimal paymentAmtFreeCouponLevel3 = BigDecimal.ZERO;

    @Column(name="payment_amt_special_claim_total", precision=18)
    private BigDecimal paymentAmtSpecialClaimTotal = BigDecimal.ZERO;

    @Column(name="payment_amt_special_claim_job", precision=18)
    private BigDecimal paymentAmtSpecialClaimJob = BigDecimal.ZERO;

    @Column(name="payment_amt_special_claim_part", precision=18)
    private BigDecimal paymentAmtSpecialClaimPart = BigDecimal.ZERO;

    @Column(name="payment_amt", precision=18)
    private BigDecimal paymentAmt = BigDecimal.ZERO;
}
