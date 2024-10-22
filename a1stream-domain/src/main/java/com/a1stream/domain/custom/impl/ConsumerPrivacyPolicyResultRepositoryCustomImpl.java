/**
 *
 */
package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import com.a1stream.domain.custom.ConsumerPrivacyPolicyResultRepositoryCustom;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.vo.ConsumerPrivacyPolicyResultVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
 *
 */
public class ConsumerPrivacyPolicyResultRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ConsumerPrivacyPolicyResultRepositoryCustom {

    @Override
    public String getPrivacyPolicyResultList(String siteId, String consumerRetrieve, String deleteFlag) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT cppr.agreement_result  AS agreementResult  ");
        sql.append("   FROM consumer_privacy_policy_result cppr        ");
        sql.append("  WHERE cppr.site_id = :siteId                     ");
        sql.append("    AND cppr.consumer_retrieve = :consumerRetrieve ");
        sql.append("    AND cppr.delete_flag = :deleteFlag             ");
        sql.append("  LIMIT 1                                          ");

        params.put("siteId", siteId);
        params.put("consumerRetrieve", consumerRetrieve);
        params.put("deleteFlag", deleteFlag);

        return super.queryForSingle(sql.toString(), params, String.class);
    }

    @Override
    public ConsumerPrivacyPolicyResultVO getConsumerPrivacyPolicyResultVO(String siteId, String consumerRetrieve, String deleteFlag) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT *  ");
        sql.append("   FROM consumer_privacy_policy_result cppr        ");
        sql.append("  WHERE cppr.site_id = :siteId                     ");
        sql.append("    AND cppr.consumer_retrieve = :consumerRetrieve ");
        sql.append("    AND cppr.delete_flag = :deleteFlag             ");
        sql.append("  LIMIT 1                                          ");

        params.put("siteId", siteId);
        params.put("consumerRetrieve", consumerRetrieve);
        params.put("deleteFlag", deleteFlag);

        return super.queryForSingle(sql.toString(), params, ConsumerPrivacyPolicyResultVO.class);
    }

    @Override
    public Long getResultConsumerId(CMM010302Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT consumer_id                            ");
        sql.append("     FROM consumer_privacy_policy_result         ");
        sql.append("    WHERE consumer_retrieve = :consumerRetrieve  ");
        sql.append("      AND site_id = :siteId                      ");
        sql.append(" ORDER BY last_updated desc                      ");
        sql.append("    LIMIT 1                                      ");

        params.put("siteId", form.getSiteId());
        params.put("consumerRetrieve", (form.getLastNm() + form.getMiddleNm() + form.getFirstNm() + form.getMobilePhone()).replaceAll("\\s", "").toUpperCase());

        return super.queryForSingle(sql.toString(), params, Long.class);
    }
}
