package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM071701BO;
import com.a1stream.domain.form.master.CMM071701Form;

public interface CmmServiceJobForDORepositoryCustom {

    List<CMM071701BO> findListByModelTypeCd(CMM071701Form form);

    ValueListResultBO findServiceJobByModelTypeList(ServiceJobVLForm model);

    List<ServiceJobVLBO> findServiceJobByModelTypeListWithJobId(String serviceCatgeoryId, Long modelType, List<Long> jobIdList);
}
