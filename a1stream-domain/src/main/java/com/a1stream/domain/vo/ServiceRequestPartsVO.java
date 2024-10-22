package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceRequestPartsVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceRequestPartsId;

    private Long serviceRequestId;

    private Long serviceOrderPartsId;

    private Long productId;

    private BigDecimal usedQty = BigDecimal.ZERO;

    private String paymentProductCd;

    private BigDecimal paymentProductQty = BigDecimal.ZERO;

    private BigDecimal paymentProductPrice = BigDecimal.ZERO;

    private BigDecimal paymentAmount = BigDecimal.ZERO;

    private String paymentProductReceiveDate;

    private String selectFlag;


}
