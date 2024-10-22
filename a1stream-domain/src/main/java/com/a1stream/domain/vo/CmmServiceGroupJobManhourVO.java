package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceGroupJobManhourVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceGroupJobManhourId;

    private Long cmmServiceJobId;

    private Long serviceGroupId;

    private BigDecimal manHours = BigDecimal.ZERO;

    private BigDecimal labourCost = BigDecimal.ZERO;


}
