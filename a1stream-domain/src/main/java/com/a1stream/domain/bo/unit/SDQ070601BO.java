package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

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
public class SDQ070601BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String psiDate;
    private String point;
    private String pointNm;
    private String modelCd;
    private String modelNm;
    private String colorCd;
    private String colorNm;
    private Integer initialStock;
    private Integer ymvn;
    private Integer wholeSalesIn;
    private Integer transferIn;
    private Integer retail;
    private Integer wholeSalesOut;
    private Integer transferOut;
    private Integer inTransit;
}