package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceAuthorizationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceAuthorizationId;

    private String authorizationNo;

    private String serializedItemNo;

    private Long pointId;

    private Long serializedProductId;

    private String fromDate;

    private String toDate;


}
