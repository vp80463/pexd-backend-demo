package com.a1stream.domain.custom.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ServiceSpVLBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMQ060301BO;
import com.a1stream.domain.custom.CmmSpecialClaimRepositoryCustom;
import com.a1stream.domain.form.master.CMQ060301Form;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmSpecialClaimRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements CmmSpecialClaimRepositoryCustom {

    @Override
    public ValueListResultBO findServiceSpList(BaseVLForm model) {
        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMD_NODELIMITER);

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT bulletin_no       AS bulletinNo      ");
        sql.append("      , campaign_no       AS campaignNo      ");
        sql.append("      , campaign_title    AS campaignTitle   ");
        sql.append("      , description       AS description     ");
        sql.append("      , effective_date    AS effectiveDate   ");
        sql.append("      , expired_date      AS expiredDate     ");
        sql.append("      , special_claim_id  AS specialClaimId  ");
        sql.append("   FROM cmm_special_claim                    ");
        sql.append("  WHERE 1 = 1                                ");
        sql.append("    AND site_id = :siteId                    ");
        sql.append("    AND effective_date <= :sysDate           ");
        sql.append("    AND expired_date   >= :sysDate           ");

        if (StringUtils.isNotBlank(model.getArg0())) {
            sql.append(" AND campaign_no   = :campaignNo         ");
            params.put("campaignNo", model.getArg0());
        }

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("sysDate", LocalDate.now().format(formatter));

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if (pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue   ");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<ServiceSpVLBO> result = super.queryForList(sql.toString(), params, ServiceSpVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<CMQ060301BO> findCampaignInquiryList(CMQ060301Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT bulletin_no       AS bulletinNo           ");
        sql.append("         , campaign_no       AS campaignNo           ");
        sql.append("         , campaign_type     AS campaignType         ");
        sql.append("         , campaign_title    AS campaignTitle        ");
        sql.append("         , effective_date    AS effectiveDate        ");
        sql.append("         , expired_date      AS expiredDate          ");
        sql.append("         , special_claim_id  AS campaignId           ");
        sql.append("         , description       AS campaignDescription  ");

        sql.append("      FROM cmm_special_claim                         ");

        sql.append("     WHERE site_id = :siteId                         ");

        if(StringUtils.isNotBlank(form.getCampaignNo())) {
            sql.append("   AND campaign_no like :campaignNo              ");
            params.put("campaignNo",CommonConstants.CHAR_PERCENT + form.getCampaignNo() + CommonConstants.CHAR_PERCENT);
        }

        if(StringUtils.isNotBlank(form.getValidDate())) {
            sql.append("   AND effective_date <= :validDate              ");
            sql.append("   AND expired_date >= :validDate                ");
            params.put("validDate",form.getValidDate());
        }

        sql.append("  ORDER BY bulletin_no                               ");

        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);

        return super.queryForList(sql.toString(), params, CMQ060301BO.class);
    }
}
