package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.batch.SvVinCodeTelIFBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.form.master.CMM010302Form;

public interface CmmConsumerSerialProRelationRepositoryCustom {

    List<CMM010302BO> getMotorcycleInfoList(CMM010302Form form);

    List<SVM010402ConsumerInfoBO> getMcConsumerData(Long cmmSerializedProductId, String siteId);

    SVM010402BO getOwnerConsumerByMc(Long cmmSerializedProductId);

    List<SvVinCodeTelIFBO> getAllOwnerMcByConsumerId(Long consumerId);
}
