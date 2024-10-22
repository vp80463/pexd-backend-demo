package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ServicePdiVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long servicePdiId;

    private Long serializedProductId;

    private Long consumerId;

    private String pdiPic;

    private String pdiDate;

    private String startTime;

    private String finishTime;

    private Long facilityId;

    private Long pdiPicId;

    public static ServicePdiVO create() {
        ServicePdiVO entity = new ServicePdiVO();
        entity.setServicePdiId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
