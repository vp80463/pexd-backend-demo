package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPdiVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long pdiId;

    private Long productCategoryId;

    private String description;

    private String fromDate;

    private String toDate;


}
