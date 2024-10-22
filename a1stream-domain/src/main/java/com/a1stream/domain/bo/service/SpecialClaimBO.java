package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.ServiceJobVLBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialClaimBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long symptomId;
    private String symptomCd;
    private String symptomNm;
    private String symptom;
    private Long conditionId;
    private String conditionCd;
    private String conditionNm;
    private String condition;
    private Long damagePartId;
    private String damagePartNm;
    private String damagePartCd;

    private List<ServiceJobVLBO> serviceJobList = new ArrayList<>();
}
