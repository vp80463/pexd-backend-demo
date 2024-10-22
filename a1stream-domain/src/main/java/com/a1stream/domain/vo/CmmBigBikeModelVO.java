package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmBigBikeModelVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long bigBikeModelId;

    private String coupCtgCd;

    private String plCd;

    private Long coupCtgLevel;

    private String userMonthFrom;

    private String userMonthTo;

    private String mileageFrom;

    private String mileageTo;

    private String modelCd;


}
