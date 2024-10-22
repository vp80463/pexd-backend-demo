package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SystemParameterVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long systemParameterId;

    private String parameterValue;

    private String systemParameterTypeId;

    private Long facilityId;
}
