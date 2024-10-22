package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.CmmMstOrganizationRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
* CmmMstOrganization table customer search function
*
* @author mid1439
*/
public class CmmMstOrganizationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmMstOrganizationRepositoryCustom {

    /**
     * @author Peng Zhengan
     */
    @Override
    public ValueListResultBO findCustomerValueList(BaseVLForm model, String siteId) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT organization_cd AS code,        ");
        sql.append("            organization_nm AS name,        ");
        sql.append("            organization_id AS id  ,        ");
        sql.append("            concat(organization_cd, ' ', organization_nm)   AS desc   ");
        sql.append("     FROM  cmm_mst_organization             ");
        sql.append("     WHERE site_id = :cmmSiteId             ");
        sql.append("     AND   organization_type = :orgType     ");
//        sql.append("     AND   organization_cd = :siteId       ");

        // arg0 = effectFlag
        if (CommonConstants.CHAR_Y.equals(model.getArg0())) {

            sql.append("     AND   from_date <= :sysDate        ");
            sql.append("     AND   to_date   >= :sysDate        ");

            params.put("sysDate", LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)));
        }

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND organization_retrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().trim().toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("     ORDER BY organization_cd               ");

        params.put("cmmSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
//        params.put("siteId", siteId);
        params.put("orgType", PJConstants.OrgRelationType.CONSUMER.getCodeDbid());

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if ( pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue ");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }
}
