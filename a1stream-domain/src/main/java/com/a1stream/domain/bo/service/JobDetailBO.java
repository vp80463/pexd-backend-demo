package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceCategoryId;
    private Long serviceDemandId;
    private String serviceDemandContent;
    private String settleTypeId;
    private Long symptomId;
    private Long jobId;
    private String jobCd;
    private String jobNm;
    private BigDecimal manhour;
    private BigDecimal standardPrice;
    private BigDecimal discountAmt;
    private BigDecimal discount;
    private BigDecimal specialPrice;
    private BigDecimal sellingPrice;
    private BigDecimal taxRate;
    private Long servicePackageId;
    private String servicePackageCd;
    private String servicePackageNm;
    private String updateCounter;

    private Long serviceOrderJobId;
}
