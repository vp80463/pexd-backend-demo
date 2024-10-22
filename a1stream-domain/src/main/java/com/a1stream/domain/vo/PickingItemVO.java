package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PickingItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long pickingItemId;

    private Long pickingListId;

    private String pickingListNo;

    private String seqNo;

    private String startedDate;

    private String startedTime;

    private String finishedDate;

    private String finishedTime;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal qty = BigDecimal.ZERO;

    private Long locationId;

    private String locationCd;

    private Long deliveryOrderId;

    private Long deliveryOrderItemId;

    private String productClassification;

    private Long facilityId;
}
