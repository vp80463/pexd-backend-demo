package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SalesOrderCancelHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long salesOrderCancelHistoryId;

    private Long salesOrderId;

    private String orderNo;

    private String orderDate;

    private String productClassification;

    private String orderSourceType;

    private Long entryFacilityId;

    private Long facilityId;

    private Long customerId;

    private Long cmmConsumerId;

    private String consumerNmFirst;

    private String consumerNmMiddle;

    private String consumerNmLast;

    private String consumerNmFull;

    private String mobilePhone;

    private Long salesOrderItemId;

    private Long productId;

    private String productCd;

    private String productNm;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private BigDecimal cancelQty = BigDecimal.ZERO;

    private String cancelDate;

    private String cancelTime;

    private String cancelReasonType;

    private Long cancelPicId;

    private String cancelPicNm;


}
