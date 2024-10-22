package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvSpecialClaimMasterBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String campaignNumber;

    private String campaignType;

    private String campaignTitle;

    private String effectiveDate;

    private String expiredDate;

    private String description;

    private String bulletinNo;

    private List<SpecialClaimRepairPatternIFItemBO> repairPatterns;
    private List<SpecialClaimSymptonCodeDetailBO> symptonCodes;
    private List<SpecialClaimConditionCodeDetailBO> conditionCodes;
    private List<SpecialClaimPIDRangeDetailBO> pidRanges;
}