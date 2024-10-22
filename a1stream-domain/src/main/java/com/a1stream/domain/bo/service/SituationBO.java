package com.a1stream.domain.bo.service;

import java.io.Serializable;

import com.a1stream.common.utils.ComUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SituationBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long symptomId;
    private String symptomCd;
    private String symptomNm;
    private Long conditionId;
    private String conditionCd;
    private String conditionNm;
    private String warrantyClaimFlag; // 
    private String faultStartDate;
    private Long productId; // 
    private String productCd; // 
    private String productNm; // 
    private String authorizationNo;
    private String repairDescription;
    private String symptomComment;
    private String conditionComment;
    private String processComment;
    private String dealerComment;
    private String updateCount;

    private Long serviceOrderFaultId;
    private Long serviceOrderId;

    private String symptom;
    private String condition;
    
    public void setSymptom(String symptom) {
    	this.symptom = symptom;
    }
    
    public void setCondition(String condition) {
    	this.condition = condition;
    }
    
    public String getSymptom() {
    	return ComUtil.concatFull(this.symptomCd, this.symptomNm);
    }
    
    public String getCondition() {
    	return ComUtil.concatFull(this.conditionCd, this.conditionNm);
    }
}
