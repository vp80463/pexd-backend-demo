package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PoCancelHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long poCancelHistoryId;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;

    private Long supplierId;

    private String supplierCd;

    private String supplierNm;

    private String orderNo;

    private Long purchaseOrderId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal cancelQty = BigDecimal.ZERO;

    private String cancelDate;

    private String cancelComment;

    private String productClassification;

    public static PoCancelHistoryVO create() {
        PoCancelHistoryVO entity = new PoCancelHistoryVO();
        entity.setPoCancelHistoryId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
