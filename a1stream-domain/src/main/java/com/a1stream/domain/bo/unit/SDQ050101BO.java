package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ050101BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String promoCd;
    private String promoNm;
    private String effectiveDate;
    private String expiredDate;
    private String giftNm;
    private String province;
    private String dealerCd;
    private String dealerNm;
    private String pointCd;
    private String pointNm;
    private String modelCd;
    private String modelNm;
    private String uploadDate;
    private String receivedDate;
    private String frameNo;

}
