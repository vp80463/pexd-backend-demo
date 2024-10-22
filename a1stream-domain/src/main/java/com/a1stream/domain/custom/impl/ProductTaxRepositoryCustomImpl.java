package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.custom.ProductTaxRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ProductTaxRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ProductTaxRepositoryCustom {

    @Override
    public List<PartsVLBO> findProductTaxList(List<Long> productIds) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT product_id AS id                     ");
        sql.append("      , tax_rate   AS taxRate                ");
        sql.append("   FROM product_tax pt                       ");
        sql.append("  WHERE site_id = :siteId                    ");
        sql.append("    AND product_classification = :S001PART   ");
        sql.append("    AND product_id IN (:productIds)          ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("productIds", productIds);
        return super.queryForList(sql.toString(), params, PartsVLBO.class);
    }

}
