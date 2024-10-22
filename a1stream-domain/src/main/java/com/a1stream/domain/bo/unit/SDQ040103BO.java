package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SDQ040103BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long registrationDocumentId;
    private String facilityNm;
    private String registrationDate;
    private String orderDate;
    private String modelCd;
    private String modelNm;
    private String colorNm;
    private String consumerName;
    private String frameNo;
    private String engineNo;
    private String plateNo;
    private String mobileNo;
}