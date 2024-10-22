package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptSlipItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptSlipItemId;

    private Long receiptSlipId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal receiptQty = BigDecimal.ZERO;

    private BigDecimal frozenQty = BigDecimal.ZERO;

    private BigDecimal receiptPrice = BigDecimal.ZERO;

    private String supplierInvoiceNo;

    private String purchaseOrderNo;

    private String caseNo;

    private String productClassification;

    private String colorNm;

    public static ReceiptSlipItemVO create() {
        ReceiptSlipItemVO entity = new ReceiptSlipItemVO();
        entity.setReceiptSlipItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
