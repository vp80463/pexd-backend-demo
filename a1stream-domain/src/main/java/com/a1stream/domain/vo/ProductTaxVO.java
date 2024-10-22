package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductTaxVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productTaxId;

    private Long productId;

    private String productClassification;

    private BigDecimal taxRate = BigDecimal.ZERO;
}
