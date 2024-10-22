package com.a1stream.domain.bo.common;

import com.a1stream.common.model.BaseVLBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandBO  extends BaseVLBO {

    private static final long serialVersionUID = 1L;
    private String serviceDemandId;
    private String serviceDemand;
    private String baseDateType;
    private String duePeriod;
    private String baseDateAfter;
    private String fromDate;
    private String toDate;
    private String jobCode;
}