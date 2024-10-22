package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialClaimSerialProVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialClaimSerialProId;

    private Long specialClaimId;

    private String dealerCd;

    private String facilityCd;

    private Long serializedProductId;

    private String repairPattern;

    private String frameNo;

    private String claimFlag;

    private String applyDate;


}
