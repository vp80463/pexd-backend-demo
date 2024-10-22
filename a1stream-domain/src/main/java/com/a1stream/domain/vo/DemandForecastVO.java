package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class DemandForecastVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long demandForecastId;

    private Long facilityId;

    private String targetMonth;

    private Long toProductId;

    private Long productId;

    private Long productCategoryId;

    private BigDecimal demandQuantity = BigDecimal.ZERO;
}
