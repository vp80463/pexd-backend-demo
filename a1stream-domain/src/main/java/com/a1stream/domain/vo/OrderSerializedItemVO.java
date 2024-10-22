package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class OrderSerializedItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long orderSerializedItemId;

    private Long serializedProductId;

    private Long orderItemId;

    private Long serviceOrderId;

    private Long salesOrderId;

    private BigDecimal price = BigDecimal.ZERO;

    private Integer mileage = CommonConstants.INTEGER_ZERO;


}
