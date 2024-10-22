package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpGeneral2039VO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private String abcType;

    private String customerCd;

    private String customerNm;

    private String consumerNm;

    private Integer soLine = CommonConstants.INTEGER_ZERO;

    private Integer allocateLine = CommonConstants.INTEGER_ZERO;

    private Integer boLine = CommonConstants.INTEGER_ZERO;

    private Integer cancelLine = CommonConstants.INTEGER_ZERO;

    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    private Integer poLine = CommonConstants.INTEGER_ZERO;

    private Integer poEoLine = CommonConstants.INTEGER_ZERO;

    private Integer poRoLine = CommonConstants.INTEGER_ZERO;

    private Integer poHoLine = CommonConstants.INTEGER_ZERO;

    private Integer poWoLine = CommonConstants.INTEGER_ZERO;

    private Integer poCancelLine = CommonConstants.INTEGER_ZERO;

    private Integer receiptLine = CommonConstants.INTEGER_ZERO;

    private BigDecimal soAmt = BigDecimal.ZERO;

    private BigDecimal allocatedAmt = BigDecimal.ZERO;

    private BigDecimal boAmt = BigDecimal.ZERO;

    private BigDecimal cancelAmt = BigDecimal.ZERO;

    private BigDecimal shipmentAmt = BigDecimal.ZERO;

    private BigDecimal shipmentCost = BigDecimal.ZERO;

    private BigDecimal returnAmt = BigDecimal.ZERO;

    private BigDecimal returnCost  = BigDecimal.ZERO;

    private BigDecimal receiveAmt = BigDecimal.ZERO;

    private BigDecimal poAmt = BigDecimal.ZERO;

    private BigDecimal poEoAmt = BigDecimal.ZERO;

    private BigDecimal poRoAmt = BigDecimal.ZERO;

    private BigDecimal poHoAmt = BigDecimal.ZERO;

    private BigDecimal poWoAmt = BigDecimal.ZERO;

    private BigDecimal poCancelAmt = BigDecimal.ZERO;

    private String createDatetime;
}
