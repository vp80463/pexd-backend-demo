package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SsmProductAbcBatchBO {

    private Long productid;
    private Long facilityid;
//    private String ssmupper;
//    private String ssmlower;
//    private String addleadtime;
//    private String safetyfactor;
    private String partsleadtime;
//    private String j1total;
    private String stringvalue;
}