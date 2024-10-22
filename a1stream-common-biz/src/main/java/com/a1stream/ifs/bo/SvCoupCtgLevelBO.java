package com.a1stream.ifs.bo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvCoupCtgLevelBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String coupCtgLevel;
    private String usedMonthFrom;
    private String usedMonthTo;
    private String mileageFrom;
    private String mileageTo;
}