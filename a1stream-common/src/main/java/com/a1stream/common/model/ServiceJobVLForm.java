package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceJobVLForm extends BaseVLForm {

    private String serviceJobCd;

    private String serviceJobNm;

    private String modelCode;

    private String modelType;

    private String serviceCategoryId;

    private String settleTypeId;
}
