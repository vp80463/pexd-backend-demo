package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpPartsTaxModelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partNo;
    private BigDecimal vatRate;
}