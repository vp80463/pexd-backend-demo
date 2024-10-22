package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class OrderSerialItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceItemId;

    private Long serviceOrderId;

    private Long serviceCategoryId;

    private Long settleTypeId;

    private Long serviceDamandId;

    private Long faultInfoId;

    private Long symptomId;

    private Long productId;

    private String serviceJobContent;

    private BigDecimal stdManhour = BigDecimal.ZERO;

    private BigDecimal stdPrice = BigDecimal.ZERO;

    private BigDecimal discountAmt = BigDecimal.ZERO;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private Long productPackageId;


}
