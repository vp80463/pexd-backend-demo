package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderFaultVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderFaultId;

    private Long serviceOrderId;

    private Long symptomId;

    private String symptomCd;

    private String symptomNm;

    private Long conditionId;

    private String conditionCd;

    private String conditionNm;

    private String warrantyClaimFlag;

    private String faultStartDate;

    private Long productId;

    private String productCd;

    private String productNm;

    private String authorizationNo;

    private String repairDescription;

    private String symptomComment;

    private String conditionComment;

    private String processComment;

    private String dealerComment;

    public static ServiceOrderFaultVO create(String siteId, Long serviceOrderId) {
        ServiceOrderFaultVO entity = new ServiceOrderFaultVO();
        entity.setServiceOrderFaultId(IdUtils.getSnowflakeIdWorker().nextId());
        entity.setSiteId(siteId);
        entity.setServiceOrderId(serviceOrderId);

        return entity;
    }
}
