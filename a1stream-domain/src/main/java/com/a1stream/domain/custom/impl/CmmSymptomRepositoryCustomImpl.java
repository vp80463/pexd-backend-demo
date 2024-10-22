package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.CmmSymptomRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class CmmSymptomRepositoryCustomImpl extends JpaNativeQuerySupportRepository  implements CmmSymptomRepositoryCustom {

    @Override
    public ValueListResultBO findByProductSectionId(BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT cs.symptom_id  AS id   ");
        sql.append("      , cs.symptom_cd  AS code ");
        sql.append("      , cs.description AS name ");
        sql.append("      , concat(cs.symptom_cd, ' ', cs.description) AS desc ");
        sql.append("   FROM cmm_symptom cs         ");

        sql.append("  WHERE site_id = :siteId      ");
        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        if(StringUtils.isNotBlank(model.getArg0())) {
            sql.append(" AND cs.product_section_id = :productSectionId ");
            params.put("productSectionId", Long.parseLong(model.getArg0()));
        }

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND concat(cs.symptom_cd, ' ', cs.description) LIKE :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }
        sql.append(" ORDER BY cs.symptom_cd ");

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result);
    }
}
