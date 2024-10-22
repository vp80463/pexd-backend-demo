package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010602DetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelCd;

    private String modelNm;

    private String colorNm;

    private String frameNo;

    private String engineNo;

    private String batteryId1;

    private String batteryId2;

}
