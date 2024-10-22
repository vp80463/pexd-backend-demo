package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstSeqNoInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long seqNoId;

    private Long facilityId;

    private String prefix;

    private String seqNoType;

    private Integer startNumber = CommonConstants.INTEGER_ZERO;

    private Integer maxNumber = CommonConstants.INTEGER_ZERO;

    private Integer currentNumber = CommonConstants.INTEGER_ZERO;

    private String description;

    private String identifyingCodeValue;


}
