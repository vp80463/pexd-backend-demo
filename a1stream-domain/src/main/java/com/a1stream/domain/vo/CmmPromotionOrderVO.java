package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPromotionOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long promotionOrderId;

    private Long promotionListId;

    private String siteNm;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;

    private Long localOrderId;

    private String localOrderNo;

    private Long cmmProductId;

    private String productCd;

    private String productNm;

    private Long localSerialProductId;

    private Long cmmSerialProductId;

    private Long cmmCustomerId;

    private String customerNm;

    private String frameNo;

    private String orderDate;

    private Long localInvoiceId;

    private String localInvoiceNo;

    private Long localDeliveryOrderId;

    private String localDeliveryOrderNo;

    private String winner;

    private String salesMethod;

    private Long salesPic;

    private String invoiceUploadPath;

    private String registrationCardPath;

    private String giftReceiptUploadPath1;

    private String giftReceiptUploadPath2;

    private String giftReceiptUploadPath3;

    private String luckyDrawVoucherPath;

    private String idCardPath;

    private String othersPath1;

    private String othersPath2;

    private String othersPath3;

    private String jugementStatus;

    private String rejectReason;

    private String canEnjoyPromotion;

    private String invoiceMemo;

    private String linkMemo;

    private String verificationCodeMemo;

    public static CmmPromotionOrderVO create() {
        CmmPromotionOrderVO entity = new CmmPromotionOrderVO();
        entity.setPromotionOrderId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }

}
