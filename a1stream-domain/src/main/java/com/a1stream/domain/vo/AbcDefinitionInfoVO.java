package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class AbcDefinitionInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long abcDefinitionInfoId;

    private Long productCategoryId;

    private String abcType;

    private BigDecimal percentage;

    private BigDecimal costFrom;

    private BigDecimal costTo;

    private BigDecimal targetSupplyRate = BigDecimal.ZERO;

    private BigDecimal ssmUpper = BigDecimal.ZERO;

    private BigDecimal ssmLower = BigDecimal.ZERO;

    private BigDecimal ropMonth = BigDecimal.ZERO;

    private BigDecimal roqMonth = BigDecimal.ZERO;

    private Integer addLeadtime = CommonConstants.INTEGER_ZERO;
}
