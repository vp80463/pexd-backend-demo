package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class StoringLineItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long storingLineItemId;

    private Long facilityId;

    private Long storingLineId;

    private Long locationId;

    private String locationCd;

    private BigDecimal instuctionQty = BigDecimal.ZERO;

    private BigDecimal storedQty = BigDecimal.ZERO;

    private String completedDate;

    private String completedTime;


}
