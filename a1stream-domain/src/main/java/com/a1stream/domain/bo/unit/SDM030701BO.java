package com.a1stream.domain.bo.unit;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030701BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private Long salesOrderId;

    private Long pointId;

    private String pointCd;

    private String salesDate;

    private String orderNo;

    private String modelCd;

    private String modelNm;

    private String modelType;

    private Integer displacement;

    private String frameNo;

    private String engineNo;

    private String colorNm;

    private String ownerCusTaxCode;

    private String evFlag;

    private Long productId;

    private Long serializedProductId;

    private Long batteryId1;

    private Long batteryId2;

    private String batteryNo1;

    private String batteryNo2;

    private Long ownerConsumerId;

    private String ownerLastNm;

    private String ownerMiddleNm;

    private String ownerFirstNm;

    private String ownerMobile;

    private String ownerSns;

    private String ownerGender;

    private String ownerBirthYear;

    private String ownerAge;

    private String ownerBirthDate;

    private String policyResultFlag;

    private Long ownerProvince;

    private Long ownerDistrict;

    private String ownerAddress;

    private String ownerEmail;

    private Long userConsumerId;

    private String userLastNm;

    private String userMiddleNm;

    private String userFirstNm;

    private String userMobile;

    private String userGender;

    private String userBirthYear;

    private String userAge;

    private String userBirthDate;

    private Long userProvince;

    private Long userDistrict;

    private String useType;

    private String ownerType;

    private String purchaseType;

    private String promotionFlag;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;

    private Long promotionOrderId;

    private String promotion;

    private String salesPicNm;

    private String giftDescription;

    private String specialReduceFlag;

    private String paymentMethod;

    private String deliveryAddress;

    private String invoicePrintFlag;

    private BigDecimal totalQty;

    private BigDecimal totalAmt;

    private BigDecimal totalActualQty;

    private BigDecimal totalActualAmt;

    private BigDecimal totalActualAmtNotVat;

    private BigDecimal discountAmt;

    private BigDecimal sellingPrice;

    private BigDecimal sellingPriceNotVat;

    private BigDecimal specialPrice;

    private BigDecimal actualAmt;

    private BigDecimal actualAmtNotVat;

    private String orderStatus;

    private String model;

    private String multiAddressFlag;

    private String point;

    private String pointNm;

    private String orderDate;

    private String consumerType;

    private String uConsumerType;

    private String occupation;

    private String pdiDate;

    private BigDecimal taxRate;

}
