package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM010302BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    // Motorcycle Info
    private String plateNo;
    private String brandNm;
    private String modelNm;
    private String frameNo;
    private String engineNo;
    private Long consumerId;
    private Long serializedProductId;

    // Service Detail
    private Long serviceOrderId;
    private String serviceDate;
    private String orderNo;
    private String brand;
    private String plateNoByService;
    private String serviceCategory;
    private String serviceCategoryId;
    private String serviceDemand;
    private String serviceTitle;
    private BigDecimal totalAmount;
}