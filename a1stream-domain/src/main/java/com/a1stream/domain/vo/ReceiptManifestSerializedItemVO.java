package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReceiptManifestSerializedItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long receiptManifestSerializedItemId;

    private Long receiptManifestItemId;

    private Long serializedProductId;

    private String completeFlag;

    private Long receiptSlipId;

    public static ReceiptManifestSerializedItemVO create() {
        ReceiptManifestSerializedItemVO entity = new ReceiptManifestSerializedItemVO();
        entity.setReceiptManifestSerializedItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
