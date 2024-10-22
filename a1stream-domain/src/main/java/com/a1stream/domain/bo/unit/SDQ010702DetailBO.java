package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010702DetailBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String frameNo;
    private String engineNo;
    private String barCd;
    private Long serializedProductId;
    private String modelCd;
    private String modelNm;
    private Long modelId;
    private String colorNm;
    private BigDecimal purchasePrice = BigDecimal.ZERO;

}
