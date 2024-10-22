package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVQ010401BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderDate;
    private String orderNo;
    private String orderStatus;
    private String frameNo;
    private String modelCd;
    private String modelNm;
    private String color;
    private String serviceDemand;
    private String mechanicName;
    private BigDecimal serviceAmount;
    private BigDecimal partsAmount;
    private Long serviceOrderId;
}
