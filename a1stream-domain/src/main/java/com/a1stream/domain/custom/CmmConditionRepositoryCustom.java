package com.a1stream.domain.custom;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;

public interface CmmConditionRepositoryCustom {

    ValueListResultBO findCmmConditionList(BaseVLForm model);
}
