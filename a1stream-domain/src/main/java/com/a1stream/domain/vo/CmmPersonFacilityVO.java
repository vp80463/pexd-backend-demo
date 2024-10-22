package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPersonFacilityVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long personFacilityId;

    private Long personId;

    private Long facilityId;

    private String personFacilityType;


}
