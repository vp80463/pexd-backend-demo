package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPricingComponentVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long pricingComponentId;

    private Long priceListId;

    private String priceCategoryId;

    private String orderTypeId;

    private Long productCategoryId;

    private Long productId;

    private BigDecimal price = BigDecimal.ZERO;

    private String fromDate;

    private String toDate;


}
