package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPM021401BO;
import com.a1stream.domain.custom.ReturnRequestListRepositoryCustom;
import com.a1stream.domain.form.parts.SPM021401Form;
import com.a1stream.domain.form.parts.SPM021402Form;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ReturnRequestListRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ReturnRequestListRepositoryCustom {

    @Override
    public List<SPM021401BO> getReturnRequestListList(SPM021401Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rrl.request_status         AS requestStatus         ");
        sql.append("          , rrl.request_status         AS requestStatusId       ");
        sql.append("          , rrl.return_request_list_id AS returnRequestListId   ");
        sql.append("          , rrl.recommend_date         AS recommendDate         ");
        sql.append("          , rrl.request_date           AS requestDate           ");
        sql.append("          , rrl.approved_date          AS approvedDate          ");
        sql.append("          , rrl.expire_date            AS expireDate            ");
        sql.append("       FROM return_request_list rrl                             ");
        sql.append("      WHERE rrl.site_id = :siteId                               ");
        sql.append("        AND rrl.product_classification = :productClassification ");
        sql.append("        AND rrl.facility_id = :facilityId                       ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("facilityId", form.getPointId());

        if (CollectionUtils.isNotEmpty(form.getStatus())) {
            sql.append(" AND rrl.request_status IN (:status) ");
            params.put("status", form.getStatus());
        }

        sql.append(" ORDER BY rrl.recommend_date ");

        return super.queryForList(sql.toString(), params, SPM021401BO.class);
    }

    @Override
    public SPM021401BO getReturnRequestList(SPM021402Form form, PJUserDetails uc) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT rrl.request_status         AS requestStatus         ");
        sql.append("          , rrl.request_status         AS requestStatusId       ");
        sql.append("          , rrl.return_request_list_id AS returnRequestListId   ");
        sql.append("          , rrl.recommend_date         AS recommendDate         ");
        sql.append("          , rrl.request_date           AS requestDate           ");
        sql.append("          , rrl.approved_date          AS approvedDate          ");
        sql.append("          , rrl.expire_date            AS expireDate            ");
        sql.append("          , rrl.facility_id            AS pointId               ");
        sql.append("       FROM return_request_list rrl                             ");
        sql.append("      WHERE rrl.site_id = :siteId                               ");
        sql.append("        AND rrl.product_classification = :productClassification ");
        sql.append("        AND rrl.return_request_list_id = :returnRequestListId   ");

        params.put("siteId", uc.getDealerCode());
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("returnRequestListId", form.getReturnRequestListId());

        return super.queryForSingle(sql.toString(), params, SPM021401BO.class);
    }
}
