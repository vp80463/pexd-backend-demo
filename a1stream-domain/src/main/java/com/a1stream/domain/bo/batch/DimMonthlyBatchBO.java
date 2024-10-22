package com.a1stream.domain.bo.batch;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DimMonthlyBatchBO {

    private String siteId;
    private List<String> facilitys;
    private String siteFac;
    private String groupNo;
}
