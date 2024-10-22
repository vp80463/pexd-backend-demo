package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM040201BO;
import com.a1stream.domain.form.master.CMM040201Form;
import com.a1stream.domain.form.master.CMM040202Form;

public interface CmmSectionRepositoryCustom {

    ValueListResultBO findSectionList(BaseVLForm model);

    List<CMM040201BO> findSectionInfoInquiryList(CMM040201Form form);

    Integer getCmmSectionCount(CMM040202Form form, String siteId);
}
