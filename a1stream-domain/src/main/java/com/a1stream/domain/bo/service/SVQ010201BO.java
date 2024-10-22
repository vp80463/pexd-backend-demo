package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVQ010201BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderDate;
    private String orderNo;
    private String orderStatus;
    private String consumerCd;
    private String consumerNm;
    private String lastNm;
    private String middleNm;
    private String firstNm;
    private String mobilePhone;
    private String policyResult;
    private String plateNo;
    private String modelCd;
    private String modelNm;
    private String color;
    private String serviceCategory;
    private String serviceCategoryId;
    private BigDecimal mileage;
    private String serviceDemand;
    private String receptionName;
    private String mechanicName;
    private BigDecimal serviceAmount;
    private BigDecimal partsAmount;
    private String brand;
    private String settleDate;
    private Long brandId;
    private Long serviceOrderId;
}
