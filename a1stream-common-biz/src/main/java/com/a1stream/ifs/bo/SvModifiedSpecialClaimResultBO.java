package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvModifiedSpecialClaimResultBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String claimApplicationNo;
    private String campaignNumber;
    private String beforeFrameNo;
    private String afterFrameNo;
    private String applicationDealerCode;
    private String applicationPointCode;
}