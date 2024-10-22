package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceRequestJobVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceRequestJobId;

    private Long serviceRequestId;

    private Long serviceOrderJobId;

    private Long jobId;

    private BigDecimal stdManhour = BigDecimal.ZERO;

    private BigDecimal paymentAmt = BigDecimal.ZERO;

    private String selectFlag;


}
