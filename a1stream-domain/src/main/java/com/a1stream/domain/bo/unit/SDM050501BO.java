package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050501BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long promotionOrderId;

    private Long promotionListId;

    private String promotionNm;

    private String promotionCd;

    private String fromDate;

    private String toDate;

    private String pointCd;

    private String siteId;

    private String pointNm;

    private Long pointId;

    private String orderNo;

    private String invoice;

    private String giftReceipt;

    private String idCard;

    private String registrationCard;

    private String luckyDrawVoucher;

    private String othersFlag;

    private String giftNm1;

    private String giftNm2;

    private String giftNm3;

    private String giftMax;

    private String giftReceiptSamplePath;

    private String modelCd;

    private String modelNm;

    private Long modelId;

    private String customerNm;

    private String frameNo;

    private String invoiceUploadPath;

    private String invoiceUploadPathFlag;

    private String registrationCardPath;

    private String registrationCardPathFlag;

    private String giftReceiptUploadPath1;

    private String giftReceiptUploadPath2;

    private String giftReceiptUploadPath3;

    private String giftReceiptUploadPathFlag;
    
    private String luckyDrawVoucherPath;

    private String luckyDrawVoucherPathFlag;
    
    private String idCardPath;

    private String idCardPathFlag;
    
    private String othersPath1;

    private String othersPath2;

    private String othersPath3;

    private String othersPathFlag;

    private String jugementStatus;
    
    private String uploadEndDate;

    private String invoiceMemo;

    private String linkMemo;

    private String verificationCodeMemo;

    private String winner;
    
    private String rejectReason;

    private String userType;

    private String picturePathInternal;

    private String url;

    private boolean sdWithDrawDisabled = true;

    private boolean sdApproveDisabled = true;
    
    private boolean sdRejectDisabled = true;
    
    private boolean acctApproveDisabled = true;
    
    private boolean acctRejectDisabled = true;
    
    private boolean giftReceiptDisabled = true;
    
    private boolean commitDisabled = true;
    
    private boolean pictureDisabled = false;

    private String giftNmCheck1;

    private String giftNmCheck2;

    private String giftNmCheck3;

    private String ajaxType;
}
