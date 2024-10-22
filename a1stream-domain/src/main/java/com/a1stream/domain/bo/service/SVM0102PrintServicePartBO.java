package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0102PrintServicePartBO implements Serializable {

    private static final long serialVersionUID = 1L;

    //servicePart
    private String partCd;

    private String partNm;

    private String uom;

    private BigDecimal qty;

    private String locationNo;

    private BigDecimal sellingPrice;

    private String settleTypeId;

    private BigDecimal amount;

    private BigDecimal allocatedQty;

    private BigDecimal boQty;

    private BigDecimal taxRate;

    private BigDecimal discountAmt;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}
