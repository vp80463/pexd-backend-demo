package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020302BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private String customerOrderNo;

    private String allocatedPartsNo;

    private String orderPartsNo;

    private String orderPartsNm;

    private String caseNo;

    private BigDecimal sellingPrice;

    private BigDecimal shippedQty;

    private BigDecimal shipmentAmount;

    private Long salesOrderId;

}
