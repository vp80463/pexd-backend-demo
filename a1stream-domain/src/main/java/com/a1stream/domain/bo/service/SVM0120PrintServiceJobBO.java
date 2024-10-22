package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM0120PrintServiceJobBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceJob;

    private BigDecimal stdmenhour;

    private String itemCd;

    private String itemContent;

    private String job;

    private BigDecimal stdPrice;

    private BigDecimal amount;

    private String serviceCategory;

}
