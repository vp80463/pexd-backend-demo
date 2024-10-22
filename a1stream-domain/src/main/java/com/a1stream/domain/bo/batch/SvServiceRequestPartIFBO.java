package com.a1stream.domain.bo.batch;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServiceRequestPartIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String exchangePartNo;
    private BigDecimal exchangePartQuantity;
}
