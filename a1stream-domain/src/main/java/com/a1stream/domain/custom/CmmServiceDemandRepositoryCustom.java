package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.DemandVLForm;
import com.a1stream.domain.bo.common.DemandBO;

public interface CmmServiceDemandRepositoryCustom {
    List<DemandBO> searchServiceDemandList(DemandVLForm model);
}
