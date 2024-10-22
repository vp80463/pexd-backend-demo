package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ060301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String bulletinNo;
    private String campaignNo;
    private String campaignType ;
    private String campaignTitle ;
    private String effectiveDate;
    private String expiredDate ;
    private Long campaignId;
    private String campaignDescription;
}