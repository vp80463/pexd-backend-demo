package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030101BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private Long orderId;
    private String orderDate;
    private String orderStatus;
    private String salesType;
    private String salesTypeDbid;
    private String pdiCheck;
    private String consumerNm;
    private String dealerCd;
    private String dealerNm;
    private Long facilityId;
    private String facilityNm;
    private BigDecimal totalQty = BigDecimal.ZERO;

}
