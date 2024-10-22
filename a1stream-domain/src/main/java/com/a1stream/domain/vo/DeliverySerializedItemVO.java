package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

@Setter
@Getter
public class DeliverySerializedItemVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long deliverySerializedItemId;

    private Long serializedProductId;

    private Long orderSerializedItemId;

    private Long deliveryOrderItemId;

    private Long deliveryOrderId;

    private BigDecimal outCost = BigDecimal.ZERO;

    public static DeliverySerializedItemVO create() {
        DeliverySerializedItemVO entity = new DeliverySerializedItemVO();
        entity.setDeliverySerializedItemId(IdUtils.getSnowflakeIdWorker().nextId());
        return entity;
    }
}
