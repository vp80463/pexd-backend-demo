package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchasePriceRequestModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestDate;

    private String siteId;

    private String productClassificationId;

    private String orderCategoryId;

    private String priceCategoryId;

    private String partyRelationTypeId;

    private Long supplierId;

    private String orderTypeId;

    private String productCategoryId;

    private Long productId;

    private BigDecimal percentage;

    private BigDecimal price;
}