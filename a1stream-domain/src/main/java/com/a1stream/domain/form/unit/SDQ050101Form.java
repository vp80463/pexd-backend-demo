package com.a1stream.domain.form.unit;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseForm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ050101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long promotionId;
    private String promotion;
    private String dateFrom;
    private String dateTo;
    private Long province;
    private Long pointId;
    private String dealer;
    private String dealerId;
    private String model;
    private Long modelId;
    private String frameNo;

    private String sdOrAccountFlag = CommonConstants.CHAR_FALSE;

}
