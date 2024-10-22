package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM030102BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String orderNo;

    private Long receiptPartsId;

    private String receiptPartsNo;

    private String receiptPartsNm;

    private Long orderPartsId;

    private String orderPartsNo;

    private String orderPartsNm;

    private BigDecimal onPurchaseQty;

    private Long purchaseOrderId;

    private BigDecimal totalReceiptQty;

    private BigDecimal receiptCost;

    private String error;

    private Long receiptManifestItemId;

    private Long receiptManifestId;

}
