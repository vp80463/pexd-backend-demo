package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010401Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Boolean pageFlg = Boolean.TRUE;

    private Long pointId;

    private String model;

    private Long modelId;

    private String plateNo;

    private Long brandId;

    private String frameNo;

    private String batteryId;

    private Long consumerId;

    private String mobilePhone;

    private List<Long> serializedProductIdList;

}
