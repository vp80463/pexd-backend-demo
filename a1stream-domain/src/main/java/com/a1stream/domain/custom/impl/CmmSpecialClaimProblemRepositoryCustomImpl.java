package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.SpecialClaimProblemCategory;
import com.a1stream.domain.bo.master.CMQ060302BO;
import com.a1stream.domain.custom.CmmSpecialClaimProblemRepositoryCustom;
import com.a1stream.domain.form.master.CMQ060302Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmSpecialClaimProblemRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements CmmSpecialClaimProblemRepositoryCustom {

    @Override
    public List<CMQ060302BO> findProblemDetailList(CMQ060302Form form, String problemCategory) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if(problemCategory.equals(SpecialClaimProblemCategory.SYMPTOM)) {

            sql.append("    SELECT cscp.problem_cd  AS SymptomCd             ");
            sql.append("         , cs.description   AS SymptomNm             ");

            sql.append("      FROM cmm_special_claim_problem cscp            ");
            sql.append(" LEFT JOIN cmm_symptom cs                            ");
            sql.append("        ON cscp.site_id = cs.site_id                 ");
            sql.append("       AND cscp.problem_cd = cs.symptom_cd           ");
        } else if(problemCategory.equals(SpecialClaimProblemCategory.CONDITION)) {

            sql.append("    SELECT cscp.problem_cd  AS conditionCd           ");
            sql.append("         , cc.description   AS conditionNm           ");

            sql.append("      FROM cmm_special_claim_problem cscp            ");
            sql.append(" LEFT JOIN cmm_condition cc                          ");
            sql.append("        ON cscp.site_id = cc.site_id                 ");
            sql.append("       AND cscp.problem_cd = cc.condition_cd         ");
        }
        sql.append("     WHERE cscp.special_claim_id = :specialClaimId   ");
        sql.append("       AND cscp.problem_category = :problemCategory  ");
        sql.append("       AND cscp.site_id = :siteId                    ");

        params.put("specialClaimId",form.getCampaignId());
        params.put("problemCategory",problemCategory);
        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);

        return super.queryForList(sql.toString(), params, CMQ060302BO.class);
    }
}