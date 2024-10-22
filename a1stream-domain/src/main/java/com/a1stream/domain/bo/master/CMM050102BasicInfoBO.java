package com.a1stream.domain.bo.master;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050102BasicInfoBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsCd;
    private String partsNm;
    private Long partsId;
    private String registrationDate;
    private String localNm;
    private String salesNm;
    private String middleGroup;
    private String middleGroupCd;
    private Long middleGroupId;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private String supersedingParts;
    private String supersedingPartsNm;
    private Long supersedingPartsId;
}