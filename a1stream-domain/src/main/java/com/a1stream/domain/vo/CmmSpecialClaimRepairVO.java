package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmSpecialClaimRepairVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long specialClaimRepairId;

    private Long specialClaimId;

    private String repairPattern;

    private String repairType;

    private String productClassification;

    private String productCd;

    private String mainDamagePartsFlag;

    private BigDecimal price = BigDecimal.ZERO;

    private BigDecimal qty = BigDecimal.ZERO;


}
