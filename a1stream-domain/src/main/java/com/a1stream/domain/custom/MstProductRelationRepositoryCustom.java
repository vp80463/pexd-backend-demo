package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.PartsVLBO;

public interface MstProductRelationRepositoryCustom {

    List<PartsVLBO> findSupersedingPartsIdList(List<Long> list);

}
