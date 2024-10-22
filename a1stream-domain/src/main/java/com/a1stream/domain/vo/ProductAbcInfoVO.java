package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductAbcInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productAbcId;

    private Long facilityId;

    private Long productId;

    private Long productCategoryId;

    private Long abcDefinitionId;

    private String abcType;


}
