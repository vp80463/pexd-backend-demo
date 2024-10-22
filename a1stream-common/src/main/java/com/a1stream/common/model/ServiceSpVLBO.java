package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceSpVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String bulletinNo;

    private String campaignNo;

    private String campaignTitle;

    private String description;

    private String effectiveDate;

    private String expiredDate;

    private Long specialClaimId;
}
