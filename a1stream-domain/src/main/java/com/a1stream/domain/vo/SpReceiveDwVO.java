package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SpReceiveDwVO {

    private String siteId;

    private String accountMonth;

    private String targetYear;

    private String targetMonth;

    private String targetDay;

    private String facilityCd;

    private String facilityNm;

    private String supplierCd;

    private String supplierNm;

    private String slipNo;

    private String inventoryTransactionTypeId;

    private String productCd;

    private String productNm;

    private String middleGroupCd;

    private String middleGroupNm;

    private String largeGroupCd;

    private String largeGroupNm;

    private String productClassification;

    private String abcType;

    private BigDecimal receiveQty = BigDecimal.ZERO;

    private BigDecimal receivePrice = BigDecimal.ZERO;

    private BigDecimal receiveAmt = BigDecimal.ZERO;

    private Integer receiveLine = CommonConstants.INTEGER_ZERO;

    private String orderMethodType;

    private String createDatetime;
}
