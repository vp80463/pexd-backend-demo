package com.a1stream.domain.vo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private CmmConsumerVO cmmConsumerVO;
    private ConsumerPrivateDetailVO consumerPrivateDetailVO;

    private Long consumerId;

    public ConsumerParam() {}

    public ConsumerParam(CmmConsumerVO cmmConsumerVO, ConsumerPrivateDetailVO consumerPrivateDetailVO, Long consumerId) {

        this.consumerPrivateDetailVO = consumerPrivateDetailVO;
        this.cmmConsumerVO = cmmConsumerVO;
        this.consumerId = consumerId;
    }
}
