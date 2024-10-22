package com.a1stream.domain.bo.service;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM020102BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    // Repair Job Detail Selection
    private String job;
    private Long serviceRequestJobId;
    private boolean jobSelectFlag;

    // Repair Parts Detail Selection
    private Long serviceRequestPartsId;
    private String parts;
    private BigDecimal partsQty;
    private boolean partsSelectFlag;

    // Repair Battery Detail Selection
    private Long serviceRequestBatteryId;
    private Long batteryId;
    private String batteryQty;
    private boolean batterySelectFlag;

    // Process History
    private String date;
    private String status;
    private String pic;
}