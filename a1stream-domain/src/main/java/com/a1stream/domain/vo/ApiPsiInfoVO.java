package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiPsiInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long psiItemId;

    private String psiId;

    private String psiDate;

    private String pointCd;

    private Long modelId;

    private String modelCd;

    private BigDecimal initialInv = BigDecimal.ZERO;

    private BigDecimal ymvnIn = BigDecimal.ZERO;

    private BigDecimal wholesalesIn = BigDecimal.ZERO;

    private BigDecimal transferIn = BigDecimal.ZERO;

    private BigDecimal retailOut = BigDecimal.ZERO;

    private BigDecimal wholesalesOut = BigDecimal.ZERO;

    private BigDecimal transferOut = BigDecimal.ZERO;

    private BigDecimal inTransit = BigDecimal.ZERO;


}
