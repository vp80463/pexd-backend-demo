package com.a1stream.domain.vo;

import java.math.BigDecimal;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmServiceHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceHistoryId;

    private String orderNo;

    private String orderDate;

    private String facilityCd;

    private String facilityNm;

    private Long productId;

    private Long serializedProductId;

    private String plateNo;

    private String frameNo;

    private Long consumerId;

    private String serviceCategory;

    private String serviceCategoryContent;

    private Long serviceDemand;

    private String serviceDemandContent;

    private String serviceSubject;

    private BigDecimal mileage = BigDecimal.ZERO;

    private String operationStartDate;

    private String operationStartTime;

    private String operationFinishDate;

    private String operationFinishTime;

    private String mechanicPicNm;

    private BigDecimal jobAmount = BigDecimal.ZERO;

    private BigDecimal partsAmount = BigDecimal.ZERO;

    private BigDecimal totalServiceAmount = BigDecimal.ZERO;

    private String commentForCustomer;

    private String customerComment;

    private String replacedPart;

    public static CmmServiceHistoryVO create() {
        CmmServiceHistoryVO entity = new CmmServiceHistoryVO();
        entity.setServiceHistoryId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
