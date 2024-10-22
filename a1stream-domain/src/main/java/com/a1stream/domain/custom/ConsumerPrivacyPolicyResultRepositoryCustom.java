package com.a1stream.domain.custom;

import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;

public interface ConsumerPrivacyPolicyResultRepositoryCustom {

    String getPrivacyPolicyResultList(String siteId, String consumerRetrieve, String deleteFlag);

    ConsumerPrivacyPolicyResultVO getConsumerPrivacyPolicyResultVO(String siteId, String consumerRetrieve, String deleteFlag);

    Long getResultConsumerId(CMM010302Form form);
}
