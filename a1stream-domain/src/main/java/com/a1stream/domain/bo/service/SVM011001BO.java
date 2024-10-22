package com.a1stream.domain.bo.service;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM011001BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String itemCd;
    private String itemNm;
    private Long pdiSettingId;
    private boolean checkFlag;
}
