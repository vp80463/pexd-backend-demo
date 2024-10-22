package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptSerializedItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptSerializedItemId;

    private Long receiptSlipItemId;

    private Long serializedProductId;

    private Long receiptSlipId;

    private String productQualityStatus;

    private String completeFlag;

    private BigDecimal inCost = BigDecimal.ZERO;

    public static ReceiptSerializedItemVO create() {
        ReceiptSerializedItemVO entity = new ReceiptSerializedItemVO();
        entity.setReceiptSerializedItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
