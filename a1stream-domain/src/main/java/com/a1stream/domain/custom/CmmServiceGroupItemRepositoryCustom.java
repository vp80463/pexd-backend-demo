package com.a1stream.domain.custom;

import java.util.List;
import java.util.Set;

import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;

public interface CmmServiceGroupItemRepositoryCustom {

    ValueListResultBO findServiceJobByModelList(ServiceJobVLForm model, Set<String> modelCode);

    List<ServiceJobVLBO> findJobListByModel(List<String> modelCdList, List<String> jobCdList, List<Long> jobIdList);
}
