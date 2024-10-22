package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ060302BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    // Repair Detail1
    private String repairPattern1;
    private String repairTypeCd1;
    private String partsNo;
    private String partsNm;
    private String primaryFlag;

    // Repair Detail2
    private String repairPattern2;
    private String repairTypeCd2;
    private String jobCd;
    private String jobNm;

    // Problem Detail1
    private String symptomCd;
    private String symptomNm;

    // Problem Detail2
    private String conditionCd;
    private String conditionNm;
}