package com.a1stream.domain.custom;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.DealerInfoBO;
import com.a1stream.common.model.ValueListResultBO;

import java.util.List;

public interface CmmSiteMasterRepositoryCustom {

    ValueListResultBO findDealerList(BaseVLForm model);

    List<DealerInfoBO> getAllDealerList(String dealerRetrieve);
}