package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmConsumerSerialProRelationVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long consumerSerializedProductRelationId;

    private Long serializedProductId;

    private Long consumerId;

    private String consumerSerializedProductRelationTypeId;

    private String ownerFlag;

    private String fromDate;

    private String toDate;

    public static CmmConsumerSerialProRelationVO create() {
        CmmConsumerSerialProRelationVO entity = new CmmConsumerSerialProRelationVO();
        entity.setConsumerSerializedProductRelationId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
