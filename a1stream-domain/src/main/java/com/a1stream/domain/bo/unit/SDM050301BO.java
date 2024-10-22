package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long promotionOrderId;

    private String modelCode;

    private String modelName;

    private String frameNo;

    private String orderNo;

    private String duNo;

    private String invoiceNo;

    private String salesMethod;

    private Long salesPic;

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

    private String judgement;
    
    private String rejectReason;

    private String promotionCode;

    private String promotionName;

    private Long promotionListId;
}
