package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptPoItemRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptPoItemRelationId;

    private Long facilityId;

    private Long orderItemId;

    private Long receiptSlipItemId;

    private String purchaseOrderNo;

    private String supplierInvoiceNo;

    private BigDecimal receiptQty = BigDecimal.ZERO;


}
