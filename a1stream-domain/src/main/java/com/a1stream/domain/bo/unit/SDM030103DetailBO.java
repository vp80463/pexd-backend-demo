package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030103DetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String modelCd;
    private String modelNm;
    private String colorNm;
    private String deliveryDate;
    private String frameNo;

}
