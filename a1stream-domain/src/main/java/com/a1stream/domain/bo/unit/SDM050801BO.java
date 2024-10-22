package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050801BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long promotionListId;

    private String promotionCd;

    private String orderNo;

    private String orderDate;

    private String frameNo;

    private String customerNm;

    private String invoiceNo;

    private String deliveryNo;

    private String siteId;

    private String siteNm;

    private String facilityCd;

    private String facilityNm;

    private Long facilityId;

    private Long productId;

    private String productCd;

    private String productNm;

    private Long serializedProductId;

    private Long cmmSerializedProductId;

    private Long consumerId;

    private String consumerFullNm;

    private Long invoiceId;

    private Long deliveryOrderId;

    private String deliveryOrderNo;

    private String salesPicNm;

    private Long salesOrderId;

    private Long salesPicId;

    //控制checkBox
    private String checkFlg;
}
