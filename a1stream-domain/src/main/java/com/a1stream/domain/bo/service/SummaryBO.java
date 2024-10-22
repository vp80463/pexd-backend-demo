package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String settleType;
    private String settleTypeId;
    private BigDecimal jobDetail;
    private BigDecimal partDetail;
    private BigDecimal batteryDetail;
    private BigDecimal total;
}
