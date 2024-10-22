package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM060101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String serviceJob;
    private String serviceJobName;
    private String englishName;
    private String salesName;
}