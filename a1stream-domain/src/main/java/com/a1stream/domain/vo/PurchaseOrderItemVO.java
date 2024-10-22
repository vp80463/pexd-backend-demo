package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PurchaseOrderItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long purchaseOrderItemId;

    private Long purchaseOrderId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal onPurchaseQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private BigDecimal transQty = BigDecimal.ZERO;

    private BigDecimal receiveQty = BigDecimal.ZERO;

    private BigDecimal storedQty = BigDecimal.ZERO;

    private BigDecimal cancelledQty = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal purchasePrice = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;

    private BigDecimal actualAmt = BigDecimal.ZERO;

    private String boCancelFlag;

    private String productClassification;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    public static PurchaseOrderItemVO create() {
        PurchaseOrderItemVO entity = new PurchaseOrderItemVO();
        entity.setPurchaseOrderItemId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
