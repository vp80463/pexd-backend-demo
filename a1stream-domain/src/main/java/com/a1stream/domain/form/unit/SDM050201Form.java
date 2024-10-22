package com.a1stream.domain.form.unit;

import java.util.List;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.unit.SDM050201BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050201Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private Long promotionListId;
    private Long promotionId;
    private String promotion;
    private String promotionCd;
    private String dateFrom;
    private String dateTo;
    private List<String> requiredDocument;
    private Integer giftMax;
    private String giftNm1;
    private String giftNm2;
    private String giftNm3;

    //暂时未删
    private String sdOrAccountFlag = CommonConstants.CHAR_FALSE;
    private Object otherProperty;

    private List<SDM050201BO> importList;
    private List<SDM050201BO> gridDataList;

}
