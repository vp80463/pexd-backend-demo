package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0102PrintServiceJobBO implements Serializable {

    private static final long serialVersionUID = 1L;

    //serviceJob
    private String jobCode;

    private String jobName;

    private BigDecimal stdManhour;

    //ServicePayment
    private String serviceCategoryId;

    private String serviceCategory;

    private String serviceDemandContent;

    private BigDecimal discount;

    private BigDecimal specialPrice;

    private BigDecimal amount;

    private String settleTypeId;

    //ServicePaymentForDO
    private BigDecimal taxRate;

    private BigDecimal stdPrice;

    private BigDecimal discountAmt;

}
