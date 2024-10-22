package com.a1stream.domain.bo.batch;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CuProductInfoBatchBO {

    private Long facilityid;
    private Long productid;
    private Integer sum;
    private BigDecimal doublevalue1;
    private BigDecimal doublevalue2;
    private Long categoryid;
    private String stringvalue1;
    private String introductiondate;
    private String supersedingPart;
}