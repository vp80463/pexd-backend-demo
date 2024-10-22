package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String sourceType;
    private String orderDate;
    private BigDecimal allocatedLines = BigDecimal.ZERO;
    private BigDecimal orderLines = BigDecimal.ZERO;
    private BigDecimal orderAmt = BigDecimal.ZERO;
    private Long salesOrderId;
    private Long relatedSvOrderId;
    private String customerCd;
    private String customerNm;
    private Long customerId;
    private Long facilityId;
    private String facilityAbbr;
    private String pointCd;
    private String sourceTypeCode;
}
