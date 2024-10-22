package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM050101BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean detailFlag;
    private Boolean addFlag;

    private String promoCd;
    private String promoNm;
    private Long promotionListId;
    private String giftNm;
    private String modelCd;
    private String modelNm;
    private String invoiceFlag;
    private String regCardFlag;
    private String giftReceiptFlag;
    private String luckyDrawFlag;
    private String idCardFlag;
    private String otherFlag;
    private String fromDate;
    private String toDate;
    private String province;
    private String dealerCd;
    private String dealerNm;
    private String pointCd;
    private String pointNm;
    private String frameNo;

}
