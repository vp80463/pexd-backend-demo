package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiSvPartItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long crmOrderId;

    private String partsServiceCategory;

    private String partsServiceDemand;

    private String partsId;

    private String partsCd;

    private String partsNm;

    private BigDecimal discount = BigDecimal.ZERO;

    private BigDecimal specialPrice = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal qty = BigDecimal.ZERO;

    private String partsServiceSymptom;

    private String apiSvPartItemId;

    private String settleType;

    private BigDecimal stdPrice = BigDecimal.ZERO;


}
