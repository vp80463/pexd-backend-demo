package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceRequestVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceRequestId;

    private String requestDealerCd;

    private String requestNo;

    private String requestDate;

    private String factoryReceiptNo;

    private String factoryReceiptDate;

    private String requestType;

    private String requestStatus;

    private Long serializedProductId;

    private String serializedItemNo;

    private String soldDate;

    private String paymentMonth;

    private Long serviceOrderId;

    private String serviceDate;

    private BigDecimal paymentPartsAmt = BigDecimal.ZERO;

    private BigDecimal paymentJobAmt = BigDecimal.ZERO;

    private BigDecimal paymentTotalAmt = BigDecimal.ZERO;

    private String expiryDate;

    private Long requestFacilityId;

    private String targetMonth;

    private String situationHappenDate;

    private BigDecimal mileage = BigDecimal.ZERO;

    private Long symptomId;

    private Long conditionId;

    private String problemComment;

    private String reasonComment;

    private String repairComment;

    private String shopComment;

    private Long mainDamagePartsId;

    private Long serviceDemandId;

    private String authorizationNo;

    private Long serviceOrderFaultId;

    private String campaignNo;

    private String bulletinNo;

    public static ServiceRequestVO create() {
        ServiceRequestVO entity = new ServiceRequestVO();
        entity.setServiceRequestId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
