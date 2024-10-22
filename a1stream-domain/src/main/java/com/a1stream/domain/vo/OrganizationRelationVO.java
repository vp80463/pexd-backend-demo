package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class OrganizationRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long organizationRelationId;

    private String relationType;

    private Long fromOrganizationId;

    private Long toOrganizationId;

    private String defaultFlag;

    private String productClassification;


}
