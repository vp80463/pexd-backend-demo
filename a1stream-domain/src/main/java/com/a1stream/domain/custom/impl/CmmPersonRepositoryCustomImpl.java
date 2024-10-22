package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM020201BO;
import com.a1stream.domain.bo.master.CMM020202GridBO;
import com.a1stream.domain.custom.CmmPersonRepositoryCustom;
import com.a1stream.domain.logic.RepositoryLogic;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

public class CmmPersonRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmPersonRepositoryCustom {

    @Resource
    private RepositoryLogic repositoryLogic;

    @Override
    public List<CmmHelperBO> getEmployeeList(BaseVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = employee_list_sql(model, siteId, params);

        List<CmmHelperBO> result = super.queryForList(sql.toString(), params, CmmHelperBO.class);

        return result;
    }

    @Override
    public ValueListResultBO findEmployeeList(BaseVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = employee_list_sql(model, siteId, params);

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        repositoryLogic.appendPagePara(model.getPageSize(), model.getCurrentPage(), sql, params);

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<CMM020201BO> findEmployeeInfoList(String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT cp.person_id  AS personId        ");
        sql.append("      , cp.person_cd  AS personCd        ");
        sql.append("      , cp.person_nm  AS personNm        ");
        sql.append("      , CASE WHEN cp.user_id IS NOT NULL ");
        sql.append("             THEN :yes                   ");
        sql.append("             ELSE :no                    ");
        sql.append("        END           AS systemUser      ");
        sql.append("      , su.user_code  AS userCd          ");
        sql.append("      , cp.from_date  AS fromDate        ");
        sql.append("      , cp.to_date    AS toDate          ");
        sql.append("      FROM cmm_person cp                 ");
        sql.append(" LEFT JOIN sys_user su                   ");
        sql.append("        ON cp.site_id = su.site_id       ");
        sql.append("       AND cp.user_id = su.user_id       ");
        sql.append("     WHERE cp.site_id = :siteId          ");
        sql.append("  ORDER BY person_cd                     ");

        params.put("siteId", siteId);
        params.put("yes", CommonConstants.CHAR_Y);
        params.put("no", CommonConstants.CHAR_N);

        return super.queryForList(sql.toString(), params, CMM020201BO.class);
    }

    @Override
    public List<CMM020202GridBO> getPointDetail(Long employeeId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mf.facility_id         AS facilityId       ");
        sql.append("      , mf.facility_cd         AS facilityCd       ");
        sql.append("      , mf.facility_nm         AS facilityNm       ");
        sql.append("      , cpf.person_facility_id AS personFacilityId ");
        sql.append("       FROM cmm_person cp                          ");
        sql.append(" INNER JOIN cmm_person_facility cpf                ");
        sql.append("         ON cp.site_id = cpf.site_id               ");
        sql.append("        AND cp.person_id = cpf.person_id           ");
        sql.append(" INNER JOIN mst_facility mf                        ");
        sql.append("         ON cpf.site_id = mf.site_id               ");
        sql.append("        AND cpf.facility_id = mf.facility_id       ");
        sql.append("      WHERE cp.site_id = :siteId                   ");
        sql.append("        AND cp.person_id = :employeeId             ");
        sql.append("   ORDER BY mf.facility_cd                         ");

        params.put("siteId", siteId);
        params.put("employeeId", employeeId);

        return super.queryForList(sql.toString(), params, CMM020202GridBO.class);
    }

	private StringBuilder employee_list_sql(BaseVLForm model, String siteId, Map<String, Object> params) {

		StringBuilder sql = new StringBuilder();

        sql.append(" SELECT person_id  as id   ");
        sql.append("      , person_cd  as code ");
        sql.append("      , person_nm  as name ");
        sql.append("      , concat(person_cd, ' ', person_nm)  as desc ");
        sql.append("   FROM cmm_person      ");
        sql.append("  WHERE site_id = :siteId  ");

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND concat(person_cd, ' ', person_nm) like :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }
        params.put("siteId", siteId);

        sql.append(" ORDER BY person_cd ");

		return sql;
	}
}
