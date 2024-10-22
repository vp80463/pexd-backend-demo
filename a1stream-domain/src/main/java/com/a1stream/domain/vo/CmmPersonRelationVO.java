package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPersonRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long relationId;

    private Long personId;

    private Long organizationId;


}
