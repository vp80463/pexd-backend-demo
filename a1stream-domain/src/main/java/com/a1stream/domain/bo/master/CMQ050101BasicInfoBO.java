package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMQ050101BasicInfoBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String supersededPartsNo;
    private String supersededPartsNm;
    private Long supersededPartsId;
}