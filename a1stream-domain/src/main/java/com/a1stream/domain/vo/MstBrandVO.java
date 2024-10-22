package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstBrandVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long brandId;

    private String brandCd;

    private String brandNm;

    private String defaultFlag;


}
