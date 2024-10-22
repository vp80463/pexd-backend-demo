package com.a1stream.domain.bo.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilityBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;
}