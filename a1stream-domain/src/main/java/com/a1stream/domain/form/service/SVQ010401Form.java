package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:0KM Service Order Inquiry
*
* @author mid1341
*/
@Getter
@Setter
public class SVQ010401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String point;
    private String dateFrom;
    private String dateTo;
    private List<String> orderStatusList;
    private String orderNo;
    private Long modelId;
    private String model;
    private Long mechanicId;
    private String mechanic;
    private Long serviceJobId;
    private String serviceJob;
}
