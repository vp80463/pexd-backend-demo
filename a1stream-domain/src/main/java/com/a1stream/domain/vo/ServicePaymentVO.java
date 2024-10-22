package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePaymentVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long paymentId;

    private String factoryPaymentControlNo;

    private String factoryBudgetSettleDate;

    private String factoryDocReceiptDate;

    private String receiptDate;

    private String targetMonth;

    private String confirmDate;

    private String paymentCategory;

    private String paymentStatus;

    private String vatCd;

    private String invoiceNo;

    private String invoiceDate;

    private String serialNo;

    private String bulletinNo;

    private BigDecimal paymentAmtWarrantyClaimTotal = BigDecimal.ZERO;

    private BigDecimal paymentAmtWarrantyClaimJob = BigDecimal.ZERO;

    private BigDecimal paymentAmtWarrantyClaimPart = BigDecimal.ZERO;

    private BigDecimal paymentAmtBatteryWarranty = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponTotal = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponMajor = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponMinor = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponLevel1 = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponLevel2 = BigDecimal.ZERO;

    private BigDecimal paymentAmtFreeCouponLevel3 = BigDecimal.ZERO;

    private BigDecimal paymentAmtSpecialClaimTotal = BigDecimal.ZERO;

    private BigDecimal paymentAmtSpecialClaimJob = BigDecimal.ZERO;

    private BigDecimal paymentAmtSpecialClaimPart = BigDecimal.ZERO;

    private BigDecimal paymentAmt = BigDecimal.ZERO;
}
