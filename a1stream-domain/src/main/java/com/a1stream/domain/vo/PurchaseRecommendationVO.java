package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PurchaseRecommendationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long purchaseRecommendationId;

    private Long productId;

    private Long facilityId;

    private Long organizationId;

    private String orderTypeId;

    private BigDecimal recommendQty = BigDecimal.ZERO;

}
