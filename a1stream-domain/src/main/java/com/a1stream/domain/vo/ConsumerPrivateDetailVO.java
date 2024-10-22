package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class ConsumerPrivateDetailVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long consumerPrivateDetailId;

    private Long consumerId;

    private String lastNm;

    private String middleNm;

    private String firstNm;

    private String consumerFullNm;

    private String consumerRetrieve;

    private String mobilePhone;

    private String mobilePhone2;

    private String mobilePhone3;

    public static ConsumerPrivateDetailVO create() {
        ConsumerPrivateDetailVO entity = new ConsumerPrivateDetailVO();
        entity.setConsumerPrivateDetailId(IdUtils.getSnowflakeIdWorker().nextId());

        return entity;
    }
}
