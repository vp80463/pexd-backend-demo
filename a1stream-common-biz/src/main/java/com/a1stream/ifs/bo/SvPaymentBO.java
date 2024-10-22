package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvPaymentBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String paymentControlNo;
    private String paymentDealerCode;
    private String dataId;
    private String ynspireAccountDate;
    private BigDecimal paymentClaimTotalAmount;
    private BigDecimal paymentCouponTotalAmount;
    private BigDecimal paymentTotalAmount;
    private String accountDocReceiptDate;

    private BigDecimal paymentClaimJobAmount;
    private BigDecimal paymentClaimPartAmount;
    private BigDecimal paymentClaimBatteryAmount;
    private BigDecimal paymentCouponLevel1Amount;
    private BigDecimal paymentCouponLevel2Amount;
    private BigDecimal paymentCouponLevel3Amount;
    private String processType;
    private String bulletinNo;

    private String accountingMonth;
}