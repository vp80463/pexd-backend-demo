package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ011302BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String frameNo;
    private String engineNo;
    private String colorNm;
    private String receivedDate;
    private BigDecimal stockAge = BigDecimal.ZERO;
    private String batteryNo1;
    private String batteryNo2;
    private String stockStatus;
}
