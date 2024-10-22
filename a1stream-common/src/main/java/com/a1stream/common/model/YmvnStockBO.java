package com.a1stream.common.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YmvnStockBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private BigDecimal stockQty;
}
