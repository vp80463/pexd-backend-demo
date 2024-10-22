package com.a1stream.domain.custom.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.master.CMM020801BO;
import com.a1stream.domain.custom.OrganizationRelationRepositoryCustom;
import com.a1stream.domain.form.master.CMM020801Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;


public class OrganizationRelationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements OrganizationRelationRepositoryCustom {

    @Override
    public List<CMM020801BO> findTraderInfoList(CMM020801Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mo.organization_cd          AS trader             ");
        sql.append("          , mo.organization_nm          AS traderNm           ");
        sql.append("          , mo.organization_id          AS traderId           ");
        sql.append("          , mo.organization_abbr        AS abbr               ");
        sql.append("          , MAX(CASE WHEN or2.relation_type = :CONSUMER THEN 'Y' ELSE 'N' END) AS dealerSign         ");
        sql.append("          , MAX(CASE WHEN or2.relation_type = :SUPPLIER THEN 'Y' ELSE 'N' END) AS supplierSign       ");
        sql.append("          , mo.from_date                AS effectiveDate      ");
        sql.append("          , mo.to_date                  AS expiredDate        ");
        sql.append("       FROM organization_relation or2                         ");
        sql.append(" INNER JOIN mst_organization mo                               ");
        sql.append("         ON mo.site_id = or2.site_id                          ");
        sql.append("        AND or2.to_organization_id = mo.organization_id       ");
        sql.append("      WHERE or2.site_id = :siteId                             ");
        sql.append("        AND or2.relation_type IN (:SUPPLIER,:CONSUMER)        ");

        if(!ObjectUtils.isEmpty(form.getPointId())) {
            sql.append("      AND or2.to_organization_id = :traderId              ");
            params.put("traderId", form.getPointId());
        }

        if (StringUtils.isNotBlank(form.getStatus())) {
            if(PJConstants.EffectStatus.EFFECTIVE.getCodeDbid().equals(form.getStatus())) {
                sql.append("   AND mo.from_date <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD')                 ");
                sql.append("   AND mo.to_date >= TO_CHAR(CURRENT_DATE, 'YYYYMMDD')                   ");
            }else {
                sql.append("   AND mo.from_date > TO_CHAR(CURRENT_DATE, 'YYYYMMDD')                  ");
                sql.append("   AND mo.to_date < TO_CHAR(CURRENT_DATE, 'YYYYMMDD')                    ");
            }
        }

        sql.append("    GROUP BY mo.organization_cd, mo.organization_nm, mo.organization_id           ");
        sql.append("           , mo.organization_abbr, mo.from_date, mo.to_date                       ");

        if (StringUtils.isNotBlank(form.getRole())) {
            sql.append("      HAVING (CASE WHEN :roleType IS NOT NULL THEN MAX(CASE WHEN or2.relation_type = :roleType THEN 1 ELSE 0 END) ELSE 1 END) = 1    ");
            params.put("roleType", form.getRole());
        }

        params.put("siteId", siteId);
        params.put("SUPPLIER", PJConstants.OrgRelationType.SUPPLIER.getCodeDbid());
        params.put("CONSUMER", PJConstants.OrgRelationType.CONSUMER.getCodeDbid());

        return super.queryForList(sql.toString(), params, CMM020801BO.class);
    }
}