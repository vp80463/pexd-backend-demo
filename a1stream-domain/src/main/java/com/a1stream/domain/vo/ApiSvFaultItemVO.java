package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiSvFaultItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long crmOrderId;

    private String symptom;

    private String conditionCd;

    private String warrantyClaim;

    private String faultStartDate;

    private String mainDamageParts;

    private String authorizationNo;

    private String repairDescription;

    private String symptomComment;

    private String conditionComment;

    private String processComment;

    private String dealerComment;

    private String apiSvFaultItemId;


}
