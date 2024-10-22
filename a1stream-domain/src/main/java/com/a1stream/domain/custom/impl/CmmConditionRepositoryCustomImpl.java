/**
 *
 */
package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.CmmConditionRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
 *
 */
public class CmmConditionRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmConditionRepositoryCustom {

    @Override
    public ValueListResultBO findCmmConditionList(BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT cc.condition_id  AS id   ");
        sql.append("      , cc.condition_cd  AS code ");
        sql.append("      , cc.description   AS name ");
        sql.append("      , concat(cc.condition_cd, ' ', cc.description)   AS desc ");
        sql.append("   FROM cmm_condition cc         ");

        sql.append("  WHERE site_id = :siteId      ");
        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND concat(cc.condition_cd, ' ', cc.description) LIKE :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }
        sql.append(" ORDER BY cc.condition_cd ");


        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result);
    }

}
