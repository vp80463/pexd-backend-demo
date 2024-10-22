package com.a1stream.domain.bo.batch;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsRecommendationBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long purchaseRecommendationId;

    private BigDecimal recommendQty;

    private String orderTypeId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal purLotQty;

    private BigDecimal minPurQty;

    private BigDecimal cost;

    private BigDecimal stdWsPrice;

    private BigDecimal reorderPoint;

    private BigDecimal reorderQty;

    private BigDecimal quantity;

    private String productStockStatusType;

    private BigDecimal monthQuantity;
}