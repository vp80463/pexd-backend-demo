package com.a1stream.domain.bo.service;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0202PrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String date;

    //ServiceExpensesClaimStatement
    private String ctlNo;
    private String dealerCode;
    private String dealerName;
    private String address;
    private String claimMonth;
    private BigDecimal partClaimAmount;
    private BigDecimal jobClaimAmount;

    //ServiceExpensesCouponStatementForEV
    private BigDecimal batteryClaimAmount;
    private String bulletinNo;

    //ServiceExpensesCouponStatement
    private BigDecimal couponAmountLevel1;
    private BigDecimal couponAmountLevel2;
    private BigDecimal couponAmountLevel3;
    private BigDecimal fir;
    private BigDecimal sec;
    private BigDecimal thi;
    private BigDecimal fou;
    private BigDecimal fif;
    private BigDecimal si;
    private BigDecimal sev;
    private BigDecimal eig;
    private BigDecimal nin;

}