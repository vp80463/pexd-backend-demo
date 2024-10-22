package com.a1stream.domain.bo.parts;

import java.math.BigDecimal;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020103PrintBO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String consumerName;
    private String orderDate;
    private Long orderId;
    private String orderPointNm;
    private String orderPointCd;
    private String multiAddressFlag;
    private String memo;
    private String companyAddress;
    private String orderType;
    private String mobilePhone;
    private String phoneNo;
    private String pointAddress;
    private BigDecimal depositAmount;
    private String salesPicNm;
    private String entryPicNm;
    private String date;
    private String wz;
    private String locationCd;
    private String partsNo;
    private String partsName;
    private BigDecimal pickQty;
    private String pickSeqNo;
    private String duNo;
    private Long duId;
    private String point;
    private String logo;
    private String invoiceNo;
    private String paymentType;
    private String employeeCd;
    private String relationShip;
    private String ticketNo;
    private String receivingPointAddress;
    private Long batteryId;
    private BigDecimal productTax;
    private String taxRate;
    private BigDecimal sellingPrice;
    private BigDecimal discountAmt;
    private String discountOffRate;
    private BigDecimal sl;
    private BigDecimal currencyVat;
    private String orderForEmployeeFlag;
    private List<SPM020103PrintDetailBO> detailList;
}
