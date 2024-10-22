package com.a1stream.domain.custom;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;

public interface CmmSymptomRepositoryCustom {

    ValueListResultBO findByProductSectionId(BaseVLForm model);
}
