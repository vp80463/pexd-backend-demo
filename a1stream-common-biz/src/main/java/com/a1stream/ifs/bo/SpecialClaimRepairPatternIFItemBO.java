package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialClaimRepairPatternIFItemBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String repairPatternCode;
    private String campaignRepairType;
    private List<SpecialClaimPartDetailBO> parts;
    private List<SpecialClaimJobDetailBO> jobCodes;
}