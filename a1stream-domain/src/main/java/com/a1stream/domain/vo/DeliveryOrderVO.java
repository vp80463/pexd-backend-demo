package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class DeliveryOrderVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long deliveryOrderId;

    private String deliveryOrderNo;

    private String inventoryTransactionType;

    private String deliveryStatus;

    private Long fromOrganizationId;

    private Long toOrganizationId;

    private Long fromFacilityId;

    private Long toFacilityId;

    private Long toConsumerId;

    private BigDecimal totalQty = BigDecimal.ZERO;

    private BigDecimal totalAmt = BigDecimal.ZERO;

    private String activityFlag;

    private String salesReturnReasonId;

    private String deliveryOrderDate;

    private String packingDate;

    private String finishDate;

    private String comment;

    private Long consigneeId;

    private String productClassification;

    private String orderSourceType;

    private String dropShipFlag;

    private String orderToType;

    private Long customerId;

    private String customerNm;

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

    private BigDecimal totalAmtNotVat = BigDecimal.ZERO;

    private Long entryPicId;

    private String entryPicNm;

    private Long pickPicId;

    private String customerCd;

    public static DeliveryOrderVO create() {
        DeliveryOrderVO entity = new DeliveryOrderVO();
        entity.setDeliveryOrderId(IdUtils.getSnowflakeIdWorker().nextId());
        return entity;
    }
}
