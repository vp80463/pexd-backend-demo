package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ApiMcSalesOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long crmOrderId;

    private String crmOrderNo;

    private String dealerCd;

    private String pointCd;

    private String salesDate;

    private String einvoiceFlag;

    private String frameNo;

    private String registrationDate;

    private String idNo;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String businessNm;

    private String mobilePhone;

    private String sns;

    private String gender;

    private String dateOfBirth;

    private String provinceCd;

    private String districtCd;

    private String email;

    private String address;

    private String occupation;

    private String paymentMethod;

    private String cusTaxCd;

    private String dealerType;

    private String orderType;

    private BigDecimal sellingPrice = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private BigDecimal taxAmount = BigDecimal.ZERO;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal amount = BigDecimal.ZERO;

    private String crmConsumerId;


}
