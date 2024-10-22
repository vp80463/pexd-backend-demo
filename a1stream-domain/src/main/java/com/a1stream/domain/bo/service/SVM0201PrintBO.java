package com.a1stream.domain.bo.service;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0201PrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String facilityCode;

    private String facilityName;

    private String claimNo;

    private String claimDate;

    private BigDecimal mileage;

    private String campaignNo;

    private String frameNo;

    private String saleDate;

    private String symptom;

    private String symptomComment;

    private List<SVM0201PrintDetailBO> details;

    private String date;

    private String batteryId;

    private String newBatteryId;

}
