package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsSendPoModelBO {

    private String requestId;
    private String sysOwnerCd;
    private String dealerCd;
    private String boCancelSign;
    private String consigneeCd;
    private String orderNo;
    private String orderType;
    private String partNo;
    private BigDecimal orderQty;
    private String requestPassword;
}
