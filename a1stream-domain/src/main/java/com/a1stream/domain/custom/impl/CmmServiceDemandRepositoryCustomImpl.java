package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.model.DemandVLForm;
import com.a1stream.domain.bo.common.DemandBO;
import com.a1stream.domain.custom.CmmServiceDemandRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmServiceDemandRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmServiceDemandRepositoryCustom {

    @Override
    public List<DemandBO> searchServiceDemandList(DemandVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT csd.service_demand_id AS serviceDemandId ");
        sql.append("      , csd.description       AS serviceDemand   ");
        sql.append("      , csd.base_date_type    AS baseDateType    ");
        sql.append("      , csd.due_period        AS duePeriod       ");
        sql.append("      , csd.base_date_after   AS baseDateAfter   ");
        sql.append("   FROM cmm_service_demand csd                   ");
        sql.append("  WHERE csd.service_category = :serviceCategory  ");
        sql.append("  ORDER BY base_date_after                       ");

        //ServiceCategory
        params.put("serviceCategory", model.getArg0());

        return super.queryForList(sql.toString(), params, DemandBO.class);
    }

}
