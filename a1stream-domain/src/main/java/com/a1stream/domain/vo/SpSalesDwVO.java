package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpSalesDwVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String customerCd;

    private String customerNm;

    private String consumerNm;

    private String orderNo;

    private String orderSourceType;

    private String orderToType;

    private String productCd;

    private String productNm;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private String abcType;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal orderQty = BigDecimal.ZERO;

    private BigDecimal actualQty = BigDecimal.ZERO;

    private BigDecimal allocateQty = BigDecimal.ZERO;

    private BigDecimal boQty = BigDecimal.ZERO;

    private BigDecimal cancelQty = BigDecimal.ZERO;

    private Integer soLine = CommonConstants.INTEGER_ZERO;

    private Integer allocateLine = CommonConstants.INTEGER_ZERO;

    private Integer boLine = CommonConstants.INTEGER_ZERO;

    private Integer cancelLine = CommonConstants.INTEGER_ZERO;

    private Integer complianceLine = CommonConstants.INTEGER_ZERO;

    private String createDatetime;

    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;
}
