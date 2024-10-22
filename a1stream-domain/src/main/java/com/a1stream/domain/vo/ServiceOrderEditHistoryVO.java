package com.a1stream.domain.vo;

import java.time.LocalDateTime;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServiceOrderEditHistoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long serviceOrderEditHistoryId;

    private Long serviceOrderId;

    private String operationDesc;

    private LocalDateTime processTime;

    private Long cmmOperatorId;

    private String operatorCd;

    private String operatorNm;

    public static ServiceOrderEditHistoryVO create(String siteId, Long serviceOrderId) {
        ServiceOrderEditHistoryVO entity = new ServiceOrderEditHistoryVO();
        entity.setSiteId(siteId);
        entity.setServiceOrderId(serviceOrderId);
        entity.setProcessTime(LocalDateTime.now());
        return entity;
    }
}
