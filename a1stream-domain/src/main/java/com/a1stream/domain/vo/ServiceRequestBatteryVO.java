package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class ServiceRequestBatteryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceRequestBatteryId;

    private Long serviceRequestId;

    private Long serviceOrderBatteryId;

    private Long batteryProductId;

    private String batteryCd;

    private String batteryNo;

    private Long batteryId;

    private BigDecimal usedQty = BigDecimal.ZERO;

    private BigDecimal paymentAmt = BigDecimal.ZERO;

    private String selectFlag;
}
