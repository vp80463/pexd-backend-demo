package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050301BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String largeGroupCd;
    private String largeGroupNm;
    private String largeGroupId;
    private String middleGroupCd;
    private String middleGroupNm;
    private String middleGroupId;
    private String dropDownNm;

}