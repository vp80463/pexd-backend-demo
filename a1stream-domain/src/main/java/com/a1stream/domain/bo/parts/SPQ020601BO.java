package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020601BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long pickingListId;

    private String pickingListNo;

    private String duNo;

    private String transactionType;

    private String transactionTypeCd;

    private String instructionDate;

    private String inventoryTransactionType;

    private String customerCd;

    private String customerNm;

    private Long pickingLines;

    private String status;

    private String locationCd;

    private String productCd;

    private String productNm;

    private BigDecimal pickingQty;

    private Long deliveryOrderId;

    private String pickSeqNo;

    private BigDecimal pickQty;

    private String boxNo;

    private String wz;

}
