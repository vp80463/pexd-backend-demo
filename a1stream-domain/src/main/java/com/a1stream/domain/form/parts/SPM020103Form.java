package com.a1stream.domain.form.parts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.parts.SPM020103BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SPM020103Form extends BaseForm{

    private static final long serialVersionUID = 1L;

    private String siteId;
    private Long salesOrderId;
    private String consumerType;
    private String deliveryPlanDate;
    private String orderNo;
    private String point;
    private Long pointId;
    private String discountPercentage;
    private BigDecimal depositAmt;
    private String  memo;
    private String shipSign;
    private String eoACCSign;
    private String deliveryAddressId;
    private String deliveryAddress;
    private String orderStatus;
    private String spPurchaseFlag;
    private String multiAddressFlag;
    private String doFlag;
    private String createEmployeeSign;
    private String employeeCd;
    private String relationship;
    private String ticketNo;
    private BigDecimal taxRate =BigDecimal.ZERO;

    private String cosnumer;
    private String firstNm;
    private String middleNm;
    private String lastNm;
    private Long consumerId;
    private String mobilePhone;
    private String custaxCd;
    private String address;
    private String sns;
    private String email;
    private String gender;
    private String birthYear;
    private String birthDay;
    private String paymentMethod;
    private String privacyResult;
    private Long province;
    private Long district;

    private List<SPM020103BO> allTableDataList = new ArrayList<>();
    private BaseTableData<SPM020103BO> tableDataList;
    private List<SPM020103BO> importList;
    private List<Long> batteryIdList;

    private Long facilityId;

    private Long deliveryOrderId ;

    private List<Long> invoiceIdList;

    private Integer updateCount;

    private String boFlag;
   
}