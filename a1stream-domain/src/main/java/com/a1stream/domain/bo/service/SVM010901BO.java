package com.a1stream.domain.bo.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM010901BO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long serviceOrderId;
	private Long relatedSalesOrderId;
    private String updateCounter;
	/**
	 * basic info
	 */
	private Long pointId;
    private String pointCd;
    private String pointNm;
    private String orderNo;
    private String orderDate;
    private String orderStatus;
    private String orderStatusId;
    private String doFlag;

    /**
     * motor info
     */
    private Long batteryId;
    private Long cmmBatteryId;
    private String batteryNo;
    private String soldDate;
    private Long partsId;
    private String partsCd;
    private String partsNm;
    private String parts;
    private String warrantyTerm;
    private String warrantyStartDate;
    private Long consumerId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobilephone;
    private String email;
    private String policyResultFlag;
    private String policyFileName;

    /**
     * service info
     */
//    private String serviceTitle;
    private String serviceCategoryId;
//    private Long serviceDemandId;
//    private String serviceDemand;
//    private Long mechanicId;
//    private String mechanicCd;
//    private String mechanicNm;

    private Long welcomeStaffId;
    private String welcomeStaffCd;
    private String welcomeStaffNm;

//    private Long cashierId;
//    private String cashierCd;
//    private String cashierNm;
    private String cashier;

    private String editor;
    private String startTime;
//    private String finishTime;
    private String operationStart;
    private String operationFinish;

    /**
     * change battery info
     */
    private Long newPartsId;
    private String newPartsCd;
    private String newPartsNm;
//    private String newParts;
    private Long newBatteryId;
    private String newBatteryNo;
    private BigDecimal retailPrice;
    private BigDecimal onHandQty;
    private BigDecimal allocatedQty;
    private BigDecimal boQty;
    private String newWarrantyTerm;
    private String location;
    private BigDecimal ymvnStock;
    private String batteryFlag;
    private Long salesOrderItemId;

    private BigDecimal sellingPriceNotVat;
    private BigDecimal actualAmtNotVat;


    /**
     * AR
     */
//    private BigDecimal depositAmt;
//    private String paymentMethodId;

    /**
     * Grid
     */
    private List<SituationBO> situations;
}
