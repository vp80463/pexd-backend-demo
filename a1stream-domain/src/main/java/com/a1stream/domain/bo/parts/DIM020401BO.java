package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String dealerCode;

    private String pointCode;

    private String orderNo;

    private String orderDate;

    private String orderStatus;

    private String orderParts;

    private String allocatedParts;

    private BigDecimal orderQty;

    private BigDecimal allocateQty;

    private BigDecimal boQty;

    private Long salesOrderId;

    private String serviceCategory;

    private Long storingLineId;

    private String receiptNo;

    private String receivedDate;

    private BigDecimal receiptQty;

    private BigDecimal storedQty;
}
