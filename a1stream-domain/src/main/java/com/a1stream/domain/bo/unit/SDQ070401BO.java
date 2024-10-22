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
public class SDQ070401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String psiMonth;
    private String point;
    private Integer initialStock;
    private Integer ymvn;
    private Integer wholeSalesIn;
    private Integer transferIn;
    private Integer retail;
    private Integer wholeSalesOut;
    private Integer transferOut;
    private Integer inTransit;
    private Integer endStock;
}