package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePdiItemVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePdiItemId;

    private Long servicePdiId;

    private String description;

    private String resultStatus;

    private Long pdiSettingId;


}
