package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String p;

    private String pointName;

    private String modelCode;

    private String modelName;

    private String colorName;

    private String plateNo;

    private String frameNo;

    private String engineNo;

    private String batteryId1;

    private String batteryId2;

    private String soldDate;

    private String qualityStatus;

    private String serviceFlag;

    private String brandName;

    private Long serializedProductId;

    private Long cmmSerializedProductId;

    private String salesStatus;

    private String stockStatus;

}
