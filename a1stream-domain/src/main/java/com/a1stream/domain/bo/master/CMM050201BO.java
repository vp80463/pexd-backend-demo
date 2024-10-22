package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM050201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String largeGroupCd;
    private String largeGroupNm;
    private String largeGroupId;
}