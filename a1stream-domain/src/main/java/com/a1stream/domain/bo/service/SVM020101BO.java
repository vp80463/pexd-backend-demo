package com.a1stream.domain.bo.service;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String requestDate;
    private String policyType;
    private String model;
    private String plateNo;
    private String requestNo;
    private String requestType;
    private String category;
    private String factoryReceiptNo;
    private String serviceOrderNo;
    private Long facilityId;
    private String campaignNo;
    private String bulletinNo;
    private String requestStatus;
    private String status;
    private Long serviceRequestId;
    private Long serviceOrderId;
    private Long serializedProductId;
    private String serviceCategoryId;
    private String warrantyType;
    private String warrantyProductUsage;
    private String fromDate;
    private String toDate;
    private Integer daydiff;
    private String selectFlag;
}