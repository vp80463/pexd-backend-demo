package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ030302BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String partsNo;

    private String partsNm;

    private String partsId;

    private String supersedingParts;

    private String supersedingPartsId;

    private String wz;

    private String wzId;

    private String location;

    private String locationId;

    private String binType;

    private String binTypeId;

    private String locationType;

    private String locationTypeId;

    private String mainLocationSign;

    private BigDecimal stockQty;

    private String largeGroup;

    private String middleGroup;

}
