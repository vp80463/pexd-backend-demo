package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PartsRopqMonthlyVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long partsRopqMonthlyId;

    private Long productId;

    private String ropqType;

    private String stringValue;


}
