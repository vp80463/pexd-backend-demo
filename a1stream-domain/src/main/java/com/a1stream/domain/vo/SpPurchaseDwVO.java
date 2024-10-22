package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpPurchaseDwVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String supplierCd;

    private String supplierNm;

    private String orderNo;

    private String orderPriorityType;

    private String orderMethodType;

    private String purchaseType;

    private String productCd;

    private String productNm;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private String abcType;

    private BigDecimal poQty = BigDecimal.ZERO;

    private BigDecimal poCancelQty = BigDecimal.ZERO;

    private BigDecimal standardPrice = BigDecimal.ZERO;

    private BigDecimal purchasePrice = BigDecimal.ZERO;

    private Integer poLine = CommonConstants.INTEGER_ZERO;

    private Integer poCancelLine = CommonConstants.INTEGER_ZERO;

    private String createDatetime;

    private BigDecimal amt = BigDecimal.ZERO;

    private Integer poEoLine = CommonConstants.INTEGER_ZERO;

    private Integer poRoLine = CommonConstants.INTEGER_ZERO;

    private Integer poHoLine = CommonConstants.INTEGER_ZERO;

    private Integer poWoLine = CommonConstants.INTEGER_ZERO;

    private BigDecimal poEoAmt = BigDecimal.ZERO;

    private BigDecimal poRoAmt = BigDecimal.ZERO;

    private BigDecimal poHoAmt = BigDecimal.ZERO;

    private BigDecimal poWoAmt = BigDecimal.ZERO;
}
