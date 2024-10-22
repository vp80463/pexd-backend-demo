package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.model.ConsumerVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.a1stream.domain.bo.master.CMM010301BO;
import com.a1stream.domain.bo.master.CMM010301ExportBO;
import com.a1stream.domain.bo.unit.SVM010402ConsumerInfoBO;
import com.a1stream.domain.form.master.CMM010301Form;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.form.service.SVM011001Form;

public interface CmmConsumerRepositoryCustom {

    ValueListResultBO findConsumerByUnitList(ConsumerVLForm model, String siteId);

    CmmConsumerBO findOwnerBySerialProId(String siteId, Long serialProId);

    List<CMM010301BO> findConsumerInfoList(CMM010301Form form, String siteId);

    List<CMM010301ExportBO> findConsumerExportList(CMM010301Form form, PJUserDetails uc);

    CMM010302Form getConsumerMaintenanceInfo(CMM010302Form form);

    Long getMainConsumerId(CMM010302Form form);

    SVM011001Form getConsumerInfoByFrameNo(SVM011001Form form);

    List<Long> getSerialProductIds(Long consumerId, String mobilephone, String siteId);

    SVM010402ConsumerInfoBO getConsumerBasicInfoByConsumerId(Long cmmSerializedProductId, Long consumerId, String siteId);

}
