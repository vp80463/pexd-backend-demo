package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010202ModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productCd;

    private String productNm;

    private String modelCd;

    private String modelNm;

    private String color;

    private BigDecimal currentStock;

    private BigDecimal shippedQty;
}
