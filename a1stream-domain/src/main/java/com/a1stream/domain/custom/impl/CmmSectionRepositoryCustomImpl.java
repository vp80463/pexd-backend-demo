package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM040201BO;
import com.a1stream.domain.custom.CmmSectionRepositoryCustom;
import com.a1stream.domain.form.master.CMM040201Form;
import com.a1stream.domain.form.master.CMM040202Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class CmmSectionRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmSectionRepositoryCustom {

    @Override
    public ValueListResultBO findSectionList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT product_section_id  AS id   ");
        sql.append("      , product_section_cd  AS code ");
        sql.append("      , product_section_nm  AS name ");
        sql.append("      , concat(product_section_cd, ' ', product_section_nm)  AS desc ");
        sql.append("   FROM cmm_section                 ");
        sql.append("  WHERE 1 = 1                       ");
        sql.append("   AND  site_id = :siteId           ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND concat(product_section_cd, ' ', product_section_nm) LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent() + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY product_section_cd ");

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if (pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<CMM040201BO> findSectionInfoInquiryList(CMM040201Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT cse.product_section_cd  AS sectionCd             ");
        sql.append("         , cse.product_section_nm  AS sectionNm             ");
        sql.append("         , csy.symptom_cd          AS faultSectionCd        ");
        sql.append("         , csy.description         AS faultSectionNm        ");
        sql.append("         , cse.product_section_id  AS sectionId             ");

        sql.append("      FROM cmm_section cse                                  ");
        sql.append("INNER JOIN cmm_symptom csy                                  ");
        sql.append("        ON cse.product_section_id = csy.product_section_id  ");

        sql.append("     WHERE cse.site_id = :siteId                             ");
        sql.append("       AND cse.product_section_id = :productSectionId        ");

        if(form.getFaultSectionId() != null) {
            sql.append("   AND csy.symptom_id = :faultSectionId                  ");
            params.put("faultSectionId",form.getFaultSectionId());
        }

        sql.append("  ORDER BY sectionCd,faultSectionCd                          ");

        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productSectionId",form.getSectionId());


        return super.queryForList(sql.toString(), params, CMM040201BO.class);
    }

    @Override
    public Integer getCmmSectionCount(CMM040202Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                  ");
        sql.append("   FROM cmm_section                               ");
        sql.append("  WHERE site_id = :siteId                         ");
        sql.append("    AND product_section_cd = :productSectionCd    ");

        params.put("siteId", siteId);
        params.put("productSectionCd", form.getSectionCd());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }
}
