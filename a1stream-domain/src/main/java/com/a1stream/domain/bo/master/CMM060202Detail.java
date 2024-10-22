package com.a1stream.domain.bo.master;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060202Detail implements Serializable {

    private static final long serialVersionUID = 1L;

    // Product Category Detail
    private Long svPackageCtgId;
    private Long categoryId;
    private String categoryCd;
    private String categoryNm;

    private Long productId;
    private Long packageItemId;
    private String productCd;
    private String productNm;
    private BigDecimal qty;

    private String productClsType;
}