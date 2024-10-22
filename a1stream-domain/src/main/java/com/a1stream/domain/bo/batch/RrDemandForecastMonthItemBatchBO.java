package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RrDemandForecastMonthItemBatchBO {

    private Long productid;
    private Long toproductid;
    private Long facilityid;
    private String demandmonth;
    private String demandqty;
    private String roqmonth;
    private String ropmonth;
    private String j1total;
    private Long demandforecastid;
    private String abctype;

}