package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class DeliveryOrderItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long deliveryOrderItemId;

    private Long deliveryOrderId;

    private String productClassification;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal originalDeliveryQty = BigDecimal.ZERO;

    private BigDecimal deliveryQty = BigDecimal.ZERO;

    private BigDecimal productCost = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal amt = BigDecimal.ZERO;

    private Long orderItemId;

    private Long salesOrderId;

    private String salesOrderNo;

    private String purchaseOrderNo;

    private String supplierCaseNo;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal amtNotVat = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    public static DeliveryOrderItemVO create() {
        DeliveryOrderItemVO entity = new DeliveryOrderItemVO();
        entity.setDeliveryOrderItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
