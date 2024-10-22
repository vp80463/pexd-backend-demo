package com.a1stream.domain.form.service;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.service.SVM020102BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020102Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private String dealerCode;
    private String requestNo;
    private String requestDate;
    private String category;
    private String categoryId;
    private String campaignNo;
    private String factoryReceiptNo;
    private String factoryReceiptDate;
    private String serviceOrderNo;
    private String status;
    private String consumer;
    private String model;
    private String plateNo;
    private String frameNo;
    private String engineNo;
    private String soldDate;
    private BigDecimal mileage;
    private String bulletinNo;
    private String serviceDate;
    private String policyType;
    private Long freeCoupon;
    private String repairDate;
    private String problemDate;
    private String reception;
    private String mechanic;
    private String symptom;
    private String condition;
    private String problemComment;
    private String reasonComment;
    private String repairComment;
    private String shopComment;
    private String authorizationNo;
    private String mainDamageParts;
    private Long serviceOrderId;
    private Long serviceRequestId;
    private Long serializedProductId;

    private List<SVM020102BO> repairJobDetailList;
    private List<SVM020102BO> repairPartsDetailList;
    private List<SVM020102BO> repairBatteryDetailList;
    private List<SVM020102BO> processHistoryList;
}