package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiUsageInfoVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long apiUsageId;

    private String accessKey;

    private String secretKey;

    private String dealerCd;

    private String localSysNm;

    private String status;

    private String effectiveDate;

    private String expirationDate;

    private String description;

    private String comment;

    private String dealerType;


}
