package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ060303BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String modelCd;
    private String modelNm;
    private String frameNo;
    private String soldDate;
    private String pointCd;
    private String applyDate;
}