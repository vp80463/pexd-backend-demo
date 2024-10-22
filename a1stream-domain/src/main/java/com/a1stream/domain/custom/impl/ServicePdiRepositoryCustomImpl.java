package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import com.a1stream.domain.custom.ServicePdiRepositoryCustom;
import com.a1stream.domain.form.service.SVM011001Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ServicePdiRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServicePdiRepositoryCustom {

    @Override
    public Integer getServicePdiRowCount(SVM011001Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                       ");
        sql.append("   FROM service_pdi                                    ");
        sql.append("  WHERE site_id = :siteId                              ");
        sql.append("    AND serialized_product_id = :serializedProductId  ");

        params.put("siteId", form.getSiteId());
        params.put("serializedProductId", form.getSerializedProductId());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }
}