package com.a1stream.common.service;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.manager.ConsumerManager;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.repository.ConsumerPrivacyPolicyResultRepository;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;

import jakarta.annotation.Resource;

@Service
public class ConsumerService {

    @Resource
    private ConsumerManager consumerMgr;
    @Resource
    private ConsumerPrivacyPolicyResultRepository consumerPrivacyPolicyResultRepo;

    public void consumerUploadPrivacy(BaseConsumerForm form) {

        consumerMgr.consumerUploadPrivacy(form);
    }

    public void saveOrUpdateConsumer(BaseConsumerForm form) {

        consumerMgr.saveOrUpdateConsumer(form);
    }

    public void saveOrUpdateConsumerPrivacyPolicyResult(BaseConsumerForm form) {

        consumerMgr.saveOrUpdateConsumerPrivacyPolicyResult(form);
    }

    public CmmConsumerBO getConsumerById(Long consumerId, String siteId) {
        return consumerMgr.getConsumerById(consumerId, siteId, CommonConstants.FALSE_CODE);
    }

    public ConsumerPrivacyPolicyResultVO timelyFindConsumerPrivacyPolicyResult(String siteId, String consumerRetrieve) {

        return consumerPrivacyPolicyResultRepo.getConsumerPrivacyPolicyResultVO(siteId, consumerRetrieve, CommonConstants.CHAR_N);
    }
}
