package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmProductTaxVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long cmmProductTaxId;

    private Long cmmProductId;

    private String productClassification;

    private BigDecimal taxRate = BigDecimal.ZERO;


}
