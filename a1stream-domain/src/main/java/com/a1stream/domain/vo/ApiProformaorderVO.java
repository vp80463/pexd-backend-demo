package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiProformaorderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long apiProformaorderId;

    private String crmOrderId;

    private String dealerCd;

    private String crmOrderNo;

    private String pointCd;

    private String partsId;

    private String partsCd;

    private BigDecimal orderQty = BigDecimal.ZERO;


}
