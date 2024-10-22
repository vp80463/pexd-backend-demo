package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvSpecialClaimApplicationBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String campaignNumber;
    private String frameNo;
    private String applicationDealerCode;
    private String applicationPointCode;
    private String applicationDate;
}