package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.DealerInfoBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.CmmSiteMasterRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class CmmSiteMasterRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmSiteMasterRepositoryCustom {

    @Override
    public ValueListResultBO findDealerList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT csm.site_id AS id         ");
        sql.append("      , csm.site_cd AS code         ");
        sql.append("      , csm.site_nm AS name         ");
        sql.append("      , concat(csm.site_cd, ' ', csm.site_nm) AS desc         ");
        sql.append("   FROM cmm_site_master csm           ");

        sql.append("  WHERE csm.active_flag = :activeFlag ");

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND concat(csm.site_cd, ' ', csm.site_nm) LIKE :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("  ORDER BY csm.site_id                ");

        params.put("activeFlag",CommonConstants.CHAR_Y);

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if ( pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append("  LIMIT :limitValue  ");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<DealerInfoBO> getAllDealerList(String dealerRetrieve) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT csm.site_id AS siteId ");
        sql.append("        , csm.site_cd AS siteCd ");
        sql.append("        , csm.site_nm AS siteNm ");
        sql.append("     FROM cmm_site_master csm   ");
        sql.append("    WHERE csm.site_id <> :site  ");
        if (!StringUtils.isEmpty(dealerRetrieve)){
            sql.append("  AND csm.site_id Like :dealerRetrieve ");
            params.put("dealerRetrieve", dealerRetrieve + "%");
        }
        sql.append(" ORDER BY csm.site_id           ");

        params.put("site", CommonConstants.CHAR_DEFAULT_SITE_ID);
        return super.queryForList(sql.toString(), params, DealerInfoBO.class);
    }
}