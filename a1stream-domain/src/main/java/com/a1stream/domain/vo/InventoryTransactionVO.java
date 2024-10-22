package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class InventoryTransactionVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long inventoryTransactionId;

    private String physicalTransactionDate;

    private String physicalTransactionTime;

    private Long fromFacilityId;

    private Long fromOrganizationId;

    private Long toOrganizationId;

    private Long toFacilityId;

    private Long productId;

    private String productCd;

    private String productNm;

    private Long targetFacilityId;

    private String relatedInventoryTransactionId;

    private Long relatedSlipId;

    private String relatedSlipNo;

    private String inventoryTransactionType;

    private String inventoryTransactionNm;

    private BigDecimal inQty = BigDecimal.ZERO;

    private BigDecimal outQty = BigDecimal.ZERO;

    private BigDecimal currentQty = BigDecimal.ZERO;

    private BigDecimal inCost = BigDecimal.ZERO;

    private BigDecimal outCost = BigDecimal.ZERO;

    private BigDecimal currentAverageCost = BigDecimal.ZERO;

    private String stockAdjustmentReasonType;

    private String stockAdjustmentReasonNm;

    private Long locationId;

    private String locationCd;

    private Long reporterId;

    private String reporterNm;

    private String comment;

    private String productClassification;

    public static InventoryTransactionVO create() {
        InventoryTransactionVO entity = new InventoryTransactionVO();
        entity.setInventoryTransactionId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
