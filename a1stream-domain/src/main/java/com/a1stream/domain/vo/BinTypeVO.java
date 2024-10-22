package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class BinTypeVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long binTypeId;

    private String binTypeCd;

    private String description;

    private Long length;

    private Long width;

    private Long height;

}
