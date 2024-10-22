package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050102BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050102Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long promotionListId;
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
    private Object otherProperty;

    private List<SDM050102BO> importList;
    private List<SDM050102BO> gridDataList;

}
