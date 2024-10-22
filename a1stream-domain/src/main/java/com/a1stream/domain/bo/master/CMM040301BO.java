package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM040301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String modelCd;
    private String modelNm;
    private String colorNm;
    private String modelYear;
    private String registeredModel;
    private String expiredDate;

}