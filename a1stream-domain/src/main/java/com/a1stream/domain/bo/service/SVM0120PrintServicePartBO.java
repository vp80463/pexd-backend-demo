package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0120PrintServicePartBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partNo;

    private BigDecimal qty;

    private String part;

    private BigDecimal stdPrice;

    private BigDecimal sellingPrice;

    private BigDecimal amount;
    
    private BigDecimal totalPrice;

}
