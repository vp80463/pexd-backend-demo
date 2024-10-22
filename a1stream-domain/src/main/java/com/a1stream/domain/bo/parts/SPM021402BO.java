package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM021402BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long returnRequestItemId;

    private String partsNo;

    private String partsNm;

    private Long partsId;

    private String parts;

    private String requestType;

    private BigDecimal recommendQty;

    private BigDecimal requestQty;

    private BigDecimal approveQty;

    private BigDecimal returnPrice;

    private BigDecimal returnAmount;

    private String yamahaInvoiceSeqNo;

    private String yamahaExternalInvoiceNo;

    private BigDecimal cost;

    private BigDecimal totalCost;

    private String mainLocation;

    private BigDecimal onHandQty;

    private BigDecimal rop;

    private BigDecimal roq;

    private String j1;

    private String j2;

    Long salesOrderItemId;

    Long salesOrderId;

    Long deliveryOrderId;
}
