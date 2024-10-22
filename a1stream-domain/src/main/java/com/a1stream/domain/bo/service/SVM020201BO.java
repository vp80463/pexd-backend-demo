package com.a1stream.domain.bo.service;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String category;

    private String displayCategory;

    private String serviceRequestPaymentId;

    private String fixDate;

    private String paymentControlNo;

    private BigDecimal paymentAmount;

    private String status;

    private String confirmDate;

    private String confirmPerson;

    private String bulletinNo;

    private String receiptDate;

    private BigDecimal freeCouponPaymentAmount;

    private BigDecimal warrantyClaimPaymentAmount;

    private BigDecimal batteryClaimPaymentAmount;

    private BigDecimal paymentTotalAmount;

    private String statementDocReceiptDate;

    private String vatCd;

    private String invoiceNo;

    private String invoiceDate;

    private String serialNo;

    private List<SVM020202BO> tableDataList;

    private Boolean disableFlg;
}