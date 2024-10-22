package com.a1stream.ifs.bo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvBigBikeBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String coupCtgCode;
    private String plCd;
    private List<SvCoupCtgLevelBO> coupCtgLevels;
    private List<SvCoupCtgBO> coupCtgModels;
}