package com.a1stream.domain.custom;

import com.a1stream.domain.bo.common.CmmConsumerBO;

public interface ConsumerPrivateDetailRepositoryCustom {

    CmmConsumerBO findConsumerPrivateDetailByConsumerId(String siteId, Long consumerId);
}