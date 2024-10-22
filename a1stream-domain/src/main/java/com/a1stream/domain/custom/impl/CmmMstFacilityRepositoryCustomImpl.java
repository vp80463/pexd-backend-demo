package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.FacilityRoleType;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.custom.CmmMstFacilityRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;


public class CmmMstFacilityRepositoryCustomImpl extends JpaNativeQuerySupportRepository
        implements CmmMstFacilityRepositoryCustom {

    @Override
    public List<CMM020501BO> findPointList(String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT facility_id        AS pointId          ");
        sql.append("        , facility_cd        AS pointCd          ");
        sql.append("        , facility_nm        AS pointNm          ");
        sql.append("        , sp_purchase_flag   AS wsDealerSign     ");
        sql.append("        , CASE facility_role_type                ");
        sql.append("            WHEN :roleTypeShop THEN :charY       ");
        sql.append("            ELSE :charN                          ");
        sql.append("          END                AS shop             ");
        sql.append("        , city_nm            AS area             ");
        sql.append("        , contact_tel        AS telephone        ");
        sql.append("        , from_date          AS effectiveDate    ");
        sql.append("        , to_date            AS expiredDate      ");
        sql.append("     FROM cmm_mst_facility cmf                   ");
        sql.append("    WHERE site_id = :siteId                      ");
        sql.append(" ORDER BY facility_cd                            ");

        params.put("siteId", siteId);
        params.put("roleTypeShop", FacilityRoleType.KEY_SHOP);
        params.put("charY", CommonConstants.CHAR_Y);
        params.put("charN", CommonConstants.CHAR_N);

        return super.queryForList(sql.toString(), params, CMM020501BO.class);
    }
}