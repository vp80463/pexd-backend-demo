package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ProductStockTakingVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productStockTakingId;

    private Long facilityId;

    private String rangeType;

    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    private Long workzoneId;

    private Long locationId;

    private Long productId;

    private BigDecimal expectedQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private String inputFlag;

    private String newFoundFlag;

    private BigDecimal currentAverageCost = BigDecimal.ZERO;

    private String picCd;

    private String picNm;

    private String startedDate;

    private String startedTime;

    private String finishedDate;

    private String finishedTime;

    private String productClassification;


}
