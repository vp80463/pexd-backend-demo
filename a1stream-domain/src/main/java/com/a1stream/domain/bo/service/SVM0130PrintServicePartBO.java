package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0130PrintServicePartBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partCd;

    private String partNm;

    private String uom;

    private BigDecimal qty;

    private BigDecimal partAmount;

    private BigDecimal sellingPrice;

}
