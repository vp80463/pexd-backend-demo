package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.bo.parts.DIM020501BO;
import com.a1stream.domain.custom.WorkzoneRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class WorkzoneRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements WorkzoneRepositoryCustom {

    /**
    * 功能描述:用于dim0205画面明细查询
    *
    * @author mid2178
    */
    @Override
    public List<DIM020501BO> getWorkzoneMaintenanceInfo(String siteId, Long personId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     select w.facility_id as pointId        ");
        sql.append("          , w.workzone_id  as workzoneId     ");
        sql.append("          , w.workzone_cd  as workzoneCd     ");
        sql.append("          , w.description  as workzoneNm     ");
        sql.append("       from workzone w                       ");
        sql.append(" inner join cmm_person_facility cpf          ");
        sql.append("         on cpf.site_id = w.site_id         ");
        sql.append("        and cpf.facility_id = w.facility_id ");
        sql.append("        and cpf.person_id = :personId        ");
        sql.append("      where w.site_id = :siteId              ");
        sql.append("   order by pointId, workzoneCd, workzoneNm    ");

        params.put("siteId",siteId);
        params.put("personId",personId);

        return super.queryForList(sql.toString(), params, DIM020501BO.class);
    }
}
