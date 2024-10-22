package com.a1stream.domain.vo;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceDemandVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceDemandId;

    private String description;

    private String baseDateType;

    private String fromDate;

    private String toDate;

    private String serviceCategory;

    private Integer duePeriod = CommonConstants.INTEGER_ZERO;

    private Integer baseDateAfter = CommonConstants.INTEGER_ZERO;


}
