package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecialClaimPIDRangeDetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stampingStyle;
    private String serialNumberFrom;
    private String serialNumberTo;
}