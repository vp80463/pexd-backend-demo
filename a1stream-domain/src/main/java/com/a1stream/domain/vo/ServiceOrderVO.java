package com.a1stream.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderId;

    private Long facilityId;

    private String facilityCd;

    private String facilityNm;

    private String orderNo;

    private String orderDate;

    private String settleDate;

    private String orderStatusId;

    private String orderStatusContent;

    private String serviceCategoryId;

    private String serviceCategoryContent;

    private Long serviceDemandId;

    private String serviceDemandContent;

    private String zeroKmFlag;

    private BigDecimal mileage = BigDecimal.ZERO;

    private LocalDateTime startTime;

    private LocalDateTime settleTime;

    private LocalDateTime operationStartTime;

    private LocalDateTime operationFinishTime;

    private Long mechanicId;

    private String mechanicCd;

    private String mechanicNm;

    private Long cashierId;

    private String cashierCd;

    private String cashierNm;

    private Long entryPicId;

    private String entryPicCd;

    private String entryPicNm;

    private Long welcomeStaffId;

    private String welcomeStaffCd;

    private String welcomeStaffNm;

    private Long cmmSerializedProductId;

    private Long brandId;

    private String brandContent;

    private String plateNo;

    private String frameNo;

    private String engineNo;

    private String color;

    private String evFlag;

    private Long modelTypeId;

    private Long modelId;

    private String modelCd;

    private String modelNm;

    private String soldDate;

    private String useTypeId;

    private String relationTypeId;

    private Long consumerId;

    private String consumerVipNo;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String consumerFullNm;

    private String mobilePhone;

    private String email;

    private String serviceSubject;

    private String mechanicComment;

    private String orderForEmployeeFlag;

    private String employeeCd;

    private String employeeRelationShipId;

    private String ticketNo;

    private String paymentMethodId;

    private BigDecimal serviceAmt = BigDecimal.ZERO;

    private BigDecimal partsAmt = BigDecimal.ZERO;

    private BigDecimal depositAmt = BigDecimal.ZERO;

    private Long relatedSalesOrderId;

    private Long cmmSpecialClaimId;

    private String bulletinNo;

    private BigDecimal serviceConsumerAmt = BigDecimal.ZERO;

    private BigDecimal partsConsumerAmt = BigDecimal.ZERO;

    private BigDecimal paymentAmt = BigDecimal.ZERO;

    public static ServiceOrderVO create() {
        ServiceOrderVO entity = new ServiceOrderVO();
        entity.setServiceOrderId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
