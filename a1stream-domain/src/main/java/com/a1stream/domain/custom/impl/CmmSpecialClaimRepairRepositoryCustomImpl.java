package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.master.CMQ060302BO;
import com.a1stream.domain.custom.CmmSpecialClaimRepairRepositoryCustom;
import com.a1stream.domain.form.master.CMQ060302Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmSpecialClaimRepairRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements CmmSpecialClaimRepairRepositoryCustom {

    @Override
    public List<CMQ060302BO> findRepairDetailList(CMQ060302Form form, String productClassification) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if(productClassification.equals(ProductClsType.PART.getCodeDbid())) {

            sql.append("    SELECT cscr.repair_pattern          AS repairPattern1           ");
            sql.append("         , cscr.repair_type             AS repairTypeCd1            ");
            sql.append("         , mp.product_cd                AS partsNo                  ");
            sql.append("         , mp.local_description         AS partsNm                  ");
            sql.append("         , cscr.main_damage_parts_flag  AS primaryFlag              ");
        } else if(productClassification.equals(ProductClsType.SERVICE.getCodeDbid())) {

            sql.append("    SELECT cscr.repair_pattern          AS repairPattern2           ");
            sql.append("         , cscr.repair_type             AS repairTypeCd2            ");
            sql.append("         , mp.product_cd                AS jobCd                    ");
            sql.append("         , mp.local_description         AS jobNm                    ");
        }
        sql.append("      FROM cmm_special_claim_repair cscr                            ");
        sql.append(" LEFT JOIN mst_product mp                                           ");
        sql.append("        ON mp.product_cd = cscr.product_cd                          ");
        sql.append("       AND mp.site_id = cscr.site_id                                ");
        sql.append("       AND mp.product_classification = cscr.product_classification  ");

        sql.append("     WHERE cscr.site_id = :siteId                                   ");
        sql.append("       AND cscr.special_claim_id = :campaignId                      ");
        sql.append("       AND cscr.product_classification = :productClassification     ");

        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("campaignId",form.getCampaignId());
        params.put("productClassification", productClassification);

        return super.queryForList(sql.toString(), params, CMQ060302BO.class);
    }
}