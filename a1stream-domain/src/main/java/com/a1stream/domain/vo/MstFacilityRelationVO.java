package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstFacilityRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long relationId;

    private Long fromFacilityId;

    private Long toFacilityId;

    private String fromDate;

    private String toDate;

    private String comment;

    private String facilityRelationTypeId;
}
