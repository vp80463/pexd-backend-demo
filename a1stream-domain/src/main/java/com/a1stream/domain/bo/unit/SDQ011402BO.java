package com.a1stream.domain.bo.unit;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ011402BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String frameNo;

    private String engineNo;

    private String batteryId1;

    private String batteryId2;

}
