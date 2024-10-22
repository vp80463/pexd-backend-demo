package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstSeqNoSettingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long seqNoSettingId;

    private Long facilityId;

    private String seqNoType;

    private String formula;

    private String identifyingCodeFormula;

    private Integer startNumber = CommonConstants.INTEGER_ZERO;

    private Integer maxNumber = CommonConstants.INTEGER_ZERO;

    private String description;

    private String complementingChar;

    private Integer seqNoLength = CommonConstants.INTEGER_ZERO;

    private Integer totalLength = CommonConstants.INTEGER_ZERO;


}
