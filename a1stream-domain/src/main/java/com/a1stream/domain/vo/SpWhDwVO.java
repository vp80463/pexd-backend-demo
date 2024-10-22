package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpWhDwVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String productClassification;

    private Integer storedPoLine = CommonConstants.INTEGER_ZERO;

    private Integer storedSoReturnLine = CommonConstants.INTEGER_ZERO;

    private Integer pickingSoLine = CommonConstants.INTEGER_ZERO;

    private Integer transferInLine = CommonConstants.INTEGER_ZERO;

    private Integer transferOutLine = CommonConstants.INTEGER_ZERO;

    private Integer adjustInLine = CommonConstants.INTEGER_ZERO;

    private Integer adjustOutLine = CommonConstants.INTEGER_ZERO;

    private Integer stocktakingLine = CommonConstants.INTEGER_ZERO;

    private String createDatetime;
}
