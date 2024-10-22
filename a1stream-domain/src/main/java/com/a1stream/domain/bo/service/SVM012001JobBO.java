package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM012001JobBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceCategoryId;
    private String settleTypeId;
    private Long jobId;
    private String jobCd;
    private String jobNm;
    private BigDecimal manhour;
    private BigDecimal standardPrice;
    private BigDecimal discount;
    private BigDecimal discountAmt;
    private BigDecimal specialPrice;
    private BigDecimal sellingPrice;
    private BigDecimal taxRate;
    private String updateCounter;

    private Long serviceOrderItemOtherBrandId;
}
