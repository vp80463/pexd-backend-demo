package com.a1stream.domain.repository;

import org.springframework.stereotype.Repository;

import com.a1stream.domain.custom.ConsumerPrivacyPolicyResultRepositoryCustom;
import com.a1stream.domain.entity.ConsumerPrivacyPolicyResult;
import com.ymsl.solid.jpa.repository.JpaExtensionRepository;


@Repository
public interface ConsumerPrivacyPolicyResultRepository extends JpaExtensionRepository<ConsumerPrivacyPolicyResult, Long>, ConsumerPrivacyPolicyResultRepositoryCustom {

    ConsumerPrivacyPolicyResult findByConsumerId(Long consumerId);

    ConsumerPrivacyPolicyResult findByConsumerFullNmAndMobilePhone(String consumerFullNm, String mobilePhone);

    ConsumerPrivacyPolicyResult findFirstByConsumerIdAndSiteId(Long consumerId, String siteId);
}
