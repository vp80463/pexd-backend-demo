package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private Long modelId;
    private String modelCd;
    private String modelNm;
    private String expiredDate;
    private String colorNm;
    private String modelYear;
    private String registeredModel;
}
