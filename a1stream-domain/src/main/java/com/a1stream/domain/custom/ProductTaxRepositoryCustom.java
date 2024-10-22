package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.PartsVLBO;

public interface ProductTaxRepositoryCustom {

    List<PartsVLBO> findProductTaxList(List<Long> productIds);

}
