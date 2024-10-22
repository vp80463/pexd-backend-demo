package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ011301BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long pointId;
    private String pointCd;
    private String pointNm;
    private Long modelId;
    private String modelCd;
    private String modelNm;
    private String modelYear;
    private String colorNm;
    private BigDecimal inTransitQty = BigDecimal.ZERO;
    private BigDecimal availableQty = BigDecimal.ZERO;
    private BigDecimal allocatedQty = BigDecimal.ZERO;
    private BigDecimal onTransferInQty = BigDecimal.ZERO;
}
