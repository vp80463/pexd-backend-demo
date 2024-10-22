package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:Service Order明细画面
*
* @author mid1341
*/
@Getter
@Setter
public class SVQ010201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private String dateType;
    private String dateFrom;
    private String dateTo;
    private String brand;
    private List<String> orderStatusList;
    private String serviceCategoryId;
    private String orderNo;
    private Long modelId;
    private String model;
    private String plateNo;
    private Long consumerId;
    private String consumer;
    private Long receptionPicId;
    private String receptionPic;
    private Long mechanicId;
    private String mechanic;
    private Long serviceJobId;
    private String serviceJob;
    private String frameNo;
    private String batteryNo;
    private String batteryClaimFlag;
}
