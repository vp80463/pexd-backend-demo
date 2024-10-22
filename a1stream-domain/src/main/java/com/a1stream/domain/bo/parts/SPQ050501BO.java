package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SPQ050501BO{

    private static final long serialVersionUID = 1L;

    private String productCd;
    private String productNm;
    private BigDecimal standardPrice;
    private Integer soLine;
    private Integer complianceLine;
}