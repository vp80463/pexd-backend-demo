package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiPartsSalesOrderItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long partsSalesOrderItemId;

    private String crmOrderId;

    private String partsId;

    private String allocatePartsCd;

    private String supersedingCd;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal stdPrice = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;


}
