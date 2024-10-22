package com.a1stream.common.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceJobVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String manHours;
    private BigDecimal stdRetailPrice;
    private BigDecimal vatRate;
}
