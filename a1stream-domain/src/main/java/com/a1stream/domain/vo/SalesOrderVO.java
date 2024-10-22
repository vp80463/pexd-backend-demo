package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class SalesOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long salesOrderId;

    private String orderNo;

    private String proformaNo;

    private String orderDate;

    private String proformaOrderDate;

    private String deliveryPlanDate;

    private String shipDate;

    private String allocateDueDate;

    private String orderStatus;

    private String orderToType;

    private String productClassification;

    private String orderPriorityType;

    private String orderSourceType;

    private Long entryFacilityId;

    private Long facilityId;

    private String facilityAddressId;

    private String facilityAddress;

    private String customerIfFlag;

    private String relatedOrderNo;

    private String dropShipType;

    private String orderType;

    private String boCancelFlag;

    private String shipCompleteFlag;

    private String demandExceptionFlag;

    private String accEoFlag;

    private String boFlag;

    private String boContactContentType;

    private String boContactDate;

    private String boContactTime;

    private Long customerId;

    private String customerNm;

    private Long consigneeId;

    private String consigneePerson;

    private String consigneeMobilePhone;

    private String consigneeAddr;

    private Long cmmConsumerId;

    private String consumerVipNo;

    private String consumerNmFirst;

    private String consumerNmMiddle;

    private String consumerNmLast;

    private String consumerNmFull;

    private String email;

    private String mobilePhone;

    private String customerTaxCode;

    private String address;

    private Long entryPicId;

    private String entryPicNm;

    private Long salesPicId;

    private String salesPicNm;

    private String authorizeNumber;

    private BigDecimal discountOffRate = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;

    private String invoicePrintFlag;

    private BigDecimal depositAmt = BigDecimal.ZERO;

    private BigDecimal totalQty = BigDecimal.ZERO;

    private BigDecimal totalAmt = BigDecimal.ZERO;

    private BigDecimal totalActualQty = BigDecimal.ZERO;

    private BigDecimal totalActualAmt = BigDecimal.ZERO;

    private String giftDescription;

    private String facilityMultiAddr;

    private String specialReduceFlag;

    private String evOrderFlag;

    private String modelCd;

    private String modelNm;

    private String frameNo;

    private String engineNo;

    private String warrantyNo;

    private String colorNm;

    private Long serializedProductId;

    private String modelType;

    private Integer displacement = CommonConstants.INTEGER_ZERO;

    private String paymentMethodType;

    private String comment;

    private Long relatedSvOrderId;

    private String orderForEmployeeFlag;

    private String employeeCd;

    private String employeeRelationShip;

    private String ticketNo;

    private String customerCd;

    private BigDecimal totalActualAmtNotVat = BigDecimal.ZERO;

    private Integer totalLine = CommonConstants.INTEGER_ZERO;

    private Long userConsumerId;

    private String employeeNm;

    public static SalesOrderVO create() {
        SalesOrderVO entity = new SalesOrderVO();
        entity.setSalesOrderId(IdUtils.getSnowflakeIdWorker().nextId());
        return entity;
    }
}
