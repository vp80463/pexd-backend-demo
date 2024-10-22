package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptManifestItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptManifestItemId;

    private Long receiptManifestId;

    private String receiptProductCd;

    private Long receiptProductId;

    private String orderProductCd;

    private Long orderProductId;

    private BigDecimal receiptQty = BigDecimal.ZERO;

    private BigDecimal receiptPrice = BigDecimal.ZERO;

    private BigDecimal storedQty = BigDecimal.ZERO;

    private BigDecimal frozenQty = BigDecimal.ZERO;

    private String purchaseOrderNo;

    private String orderPriorityType;

    private String orderMethodType;

    private String caseNo;

    private String errorFlag;

    private String errorInfo;

    private String manifestItemStatus;

    public static ReceiptManifestItemVO create() {
        ReceiptManifestItemVO entity = new ReceiptManifestItemVO();
        entity.setReceiptManifestItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
