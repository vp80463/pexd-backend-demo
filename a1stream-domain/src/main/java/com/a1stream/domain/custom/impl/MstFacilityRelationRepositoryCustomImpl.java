package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.common.FacilityBO;
import com.a1stream.domain.custom.MstFacilityRelationRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class MstFacilityRelationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstFacilityRelationRepositoryCustom {

    @Override
    public List<FacilityBO> findFacilityListByRelationType(String siteId
                                                         , Long fromFacilityId
                                                         , String facilityRelationTypeId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT mf.facility_id AS facilityId                              ");
        sql.append("        , mf.facility_cd AS facilityCd                              ");
        sql.append("        , mf.facility_nm AS facilityNm                              ");
        sql.append("     FROM mst_facility_relation mfr                                 ");
        sql.append("        , mst_facility mf                                           ");
        sql.append("    WHERE mfr.site_id                   = :siteId                   ");
        sql.append("      AND mfr.from_facility_id          = :fromFacilityId           ");
        sql.append("      AND mfr.facility_relation_type_id = :facilityRelationTypeId   ");
        sql.append("      AND mfr.site_id                   = mf.site_id                ");
        sql.append("      AND mfr.to_facility_id            = mf.facility_id            ");
        sql.append(" ORDER BY mf.facility_cd                                            ");

        params.put("siteId"                , siteId);
        params.put("fromFacilityId"        , fromFacilityId);
        params.put("facilityRelationTypeId", facilityRelationTypeId);

        return super.queryForList(sql.toString(), params, FacilityBO.class);
    }
}
