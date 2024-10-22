package com.a1stream.domain.bo.parts;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetSalesOrderItemBO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8885136782059534501L;

    private String siteId;

    private Long deliveryPointId;

    private Long salesOrderId;

    private String orderNo;

    private String orderPriorityType;

    private Long salesOrderItemId;

    private String itemSeqNo;

    private Integer prioritySeqNo;

    private String allocateDueDate;

    private Long productId;

    private Long allocateProdId;

    private BigDecimal originalQty;

    private BigDecimal waitingAllocateQty;

    private BigDecimal boQty;

    private BigDecimal changedQty;
}