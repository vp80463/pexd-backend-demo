package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述: 库存盘点时真实库存录入
*
* @author mid2215
*/
@Getter
@Setter
public class SPM030901BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long productStockTakingId;
    private Long pointId;
    private String rangeType;
    private Integer seqNo;
    private Long workzoneId;
    private String workzone;
    private Long locationId;
    private String location;
    private String locationTypeCode;
    private Long productId;
    private String productCd;
    private String productName;
    private BigDecimal actualQty;
    private BigDecimal actualQtyBefore;
    private BigDecimal currentAverageCost;
    private String inputFlag;
    private String newFoundFlag;
    private String startedDate;
    private String startedTime;
}