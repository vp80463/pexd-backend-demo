package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceGroupItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceGroupItemId;

    private String prodCd;

    private Long serviceGroupId;


}
