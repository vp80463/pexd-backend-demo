package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialClaimProblemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialClaimProblemId;

    private Long specialClaimId;

    private String problemCategory;

    private String problemCd;


}
