package com.a1stream.domain.bo.parts;

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
public class SPQ050401BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String targetDay;
    private String facilityCd;
    private String facilityNm;
    private Integer pickingSoLine;
    private Integer stocktakingLine;
    private Integer receiveLine;
    private Integer shipmentLine;
    private Integer storedLine;
    private Integer transferLine;
    private Integer adjustLine;
}