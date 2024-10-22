package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ReturnRequestItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long returnRequestItemId;

    private Long facilityId;

    private Long returnRequestListId;

    private Long productId;

    private String productCd;

    private String productNm;

    private String requestType;

    private String requestStatus;

    private String yamahaInvoiceSeqNo;

    private String yamahaExternalInvoiceNo;

    private BigDecimal recommendQty = BigDecimal.ZERO;

    private BigDecimal requestQty = BigDecimal.ZERO;

    private BigDecimal approvedQty = BigDecimal.ZERO;

    private BigDecimal returnPrice = BigDecimal.ZERO;

    private Long salesOrderItemId;

    private Long salesOrderId;

    private String productClassification;


}
