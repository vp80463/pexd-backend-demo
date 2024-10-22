package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class StoringLineVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long storingLineId;

    private Long facilityId;

    private String storingLineNo;

    private Long storingListId;

    private Long receiptSlipItemId;

    private Long productId;

    private String productCd;

    private String productNm;

    private String caseNo;

    private BigDecimal originalInstQty = BigDecimal.ZERO;

    private BigDecimal instuctionQty = BigDecimal.ZERO;

    private BigDecimal storedQty = BigDecimal.ZERO;

    private BigDecimal frozenQty = BigDecimal.ZERO;

    private String completedDate;

    private String completedTime;

    private String inventoryTransactionType;

    private String productClassification;

    private String receiptSlipNo;

    public static StoringLineVO create() {
        StoringLineVO entity = new StoringLineVO();
        entity.setStoringLineId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
