package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BatteryBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String partCd;
    private Long partId;
    private String currentBatteryNo;
    private Long currentBatteryId;
    private String warttryStartDate;
    private String warrantyTerm;
    private String newPartCd;
    private Long newPartId;
    private String newBatteryNo;
    private BigDecimal sellingPrice;
    private String batteryType;
    private String updateCounter;

    private Long serviceOrderBatteryId;
}
