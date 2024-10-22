package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductInventoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productInventoryId;

    private Long facilityId;

    private Long productId;

    private BigDecimal quantity = BigDecimal.ZERO;

    private Long locationId;

    private String productQualityStatusId;

    private String primaryFlag;

    private String productClassification;


}
