package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsAutoPurchaseOrderBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productCode;
    private String productName;
    private Long productId;
    private BigDecimal minimumPurchaseQuantity;
    private BigDecimal purchaseLotQuantity;

    private BigDecimal price;
    private BigDecimal recommendRo;
    private BigDecimal recommendEo;
    private BigDecimal roQuantity;
    private BigDecimal eoQuantity;
    private BigDecimal eoAmount;
    private BigDecimal roAmount;
    private Long purchaseRecommendationEoId;
    private Long purchaseRecommendationRoId;
}