package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMQ060303BO;
import com.a1stream.domain.custom.CmmSpecialClaimSerialProRepositoryCustom;
import com.a1stream.domain.form.master.CMQ060303Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmSpecialClaimSerialProRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements CmmSpecialClaimSerialProRepositoryCustom {

    @Override
    public List<CMQ060303BO> findCampaignResultInquiryList(CMQ060303Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mp.product_cd         AS modelCd                         ");
        sql.append("         , mp.local_description  AS modelNm                         ");
        sql.append("         , cscsp.frame_no        AS frameNo                         ");
        sql.append("         , csp.stu_date          AS soldDate                        ");
        sql.append("         , cscsp.facility_cd     AS pointCd                         ");
        sql.append("         , cscsp.apply_date      AS applyDate                       ");

        sql.append("      FROM cmm_special_claim_serial_pro cscsp                       ");
        sql.append(" LEFT JOIN cmm_serialized_product csp                               ");
        sql.append("        ON cscsp.serialized_product_id = csp.serialized_product_id  ");
        sql.append(" LEFT JOIN mst_product mp                                           ");
        sql.append("        ON mp.product_id = csp.product_id                           ");

        sql.append("     WHERE cscsp.site_id = :siteId                                  ");
        sql.append("       AND cscsp.special_claim_id = :specialClaimId                 ");

        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("specialClaimId",form.getCampaignId());

        return super.queryForList(sql.toString(), params, CMQ060303BO.class );
    }
}