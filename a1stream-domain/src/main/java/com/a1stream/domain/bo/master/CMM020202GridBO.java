package com.a1stream.domain.bo.master;

import com.a1stream.domain.vo.CmmPersonFacilityVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM020202GridBO extends CmmPersonFacilityVO {

    private static final long serialVersionUID = 1L;

    private String facilityCd;
    private String facilityNm;
}