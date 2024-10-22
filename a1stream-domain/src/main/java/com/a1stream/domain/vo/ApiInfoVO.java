package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long apiInfoId;

    private String apiNm;

    private String comment;

    private String apiType;

    private String freequance;

    private String url;


}
