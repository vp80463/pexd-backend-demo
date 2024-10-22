package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPQ020201BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String pointCd;
    private String pointNm;
    private String orderNo;
    private String orderPartsCd;
    private String orderPartsNm;
    private String allocatedPartsCd;
    private String allocatedPartsNm;
    private BigDecimal retailPrice = BigDecimal.ZERO;
    private BigDecimal sellingPrice = BigDecimal.ZERO;
    private String customerCd;
    private BigDecimal orderQty = BigDecimal.ZERO;
    private BigDecimal allocatedQty = BigDecimal.ZERO;
    private BigDecimal boQty = BigDecimal.ZERO;
    private BigDecimal pickingQty = BigDecimal.ZERO;
    private BigDecimal shippedQty = BigDecimal.ZERO;
    private BigDecimal cancelQty = BigDecimal.ZERO;
    private String cancelDate;
    private String cancelPic;
    private String orderDate;
    private Integer priority;
    private Long salesOrderId;
    private Long pointId;
    private Long orderPartsId;
    private Long allocatedPartsId;
}
