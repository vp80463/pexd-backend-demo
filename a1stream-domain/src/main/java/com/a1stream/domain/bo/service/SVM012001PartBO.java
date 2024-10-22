package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM012001PartBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceCategoryId;
    private String settleTypeId;
    private String partNm;
    private BigDecimal standardPrice;
    private BigDecimal sellingPrice;
    private BigDecimal qty;
    private BigDecimal amount;
    private BigDecimal taxRate;
    private String updateCounter;
    private BigDecimal sellingPriceNotVat;
    private BigDecimal actualAmtNotVat;

    private Long serviceOrderItemOtherBrandId;
}
