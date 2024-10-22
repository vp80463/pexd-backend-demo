package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class PartsRopqParameterVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long partsRopqParameterId;

    private Long facilityId;

    private Long productId;

    private String firstOrderDate;

    private String ropqExceptionSign;

    private String ropqManualSign;


}
