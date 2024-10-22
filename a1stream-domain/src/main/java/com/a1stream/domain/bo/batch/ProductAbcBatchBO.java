package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductAbcBatchBO{

    private Long productid;
    private Long facilityid;;
    private String abctype;
    private String ssmupper;
    private String ssmlower;
    private String addleadtime;
    private String targetsupplyrate;
    private String ropmonth;
    private String roqmonth;
    private Long productcategoryid;
    private String registerdate;
    private String firstorderdate;
    private String j2total;
    private String j1total;
    private BigDecimal trendindex;
}