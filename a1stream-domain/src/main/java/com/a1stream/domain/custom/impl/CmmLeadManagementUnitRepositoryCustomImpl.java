package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.custom.CmmLeadManagementUnitRepositoryCustom;
import com.a1stream.domain.form.unit.SVM010603Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmLeadManagementUnitRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmLeadManagementUnitRepositoryCustom {

    @Override
    public Page<SVM010603BO> pageMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = query_sql();

        StringBuilder condition = condition_sql(form, siteId, dealerCd, params);

        StringBuilder countSql = new StringBuilder();
        countSql.append(" SELECT COUNT(*) FROM ( ")
                .append(" SELECT clmu.lead_management_result_id ")
                .append(condition)
                .append(" ) AS subquery ");

        Integer count = super.queryForSingle(countSql.toString(), params, Integer.class);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());

        return new PageImpl<>(super.queryForPagingList(sql.append(condition).toString(), params, SVM010603BO.class, pageable), pageable, count);
    }

    @Override
    public List<SVM010603BO> listMcSalesLeadData(SVM010603Form form, String siteId, String dealerCd) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = query_sql();

        StringBuilder condition = condition_sql(form, siteId, dealerCd, params);

        return super.queryForList(sql.append(condition).toString(), params, SVM010603BO.class);
    }

    private StringBuilder condition_sql(SVM010603Form form, String siteId, String dealerCd, Map<String, Object> params) {

        StringBuilder condition = new StringBuilder();

        condition.append("      FROM cmm_lead_management_unit clmu                                     ");
        condition.append("     WHERE site_id = :siteId                                                 ");
        condition.append("       AND clmu.dealer_cd = :defaultPointCd                                  ");
        condition.append("       AND clmu.contact_date_from_customer >= :fromDate                      ");
        condition.append("       AND clmu.contact_date_from_customer <= :toDate                        ");

        params.put("siteId", siteId);
        params.put("defaultPointCd", dealerCd);
        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());

        if (StringUtils.isNotBlank(form.getModelCd())) {
            condition.append(" AND clmu.interested_model = :modelCd ");
            params.put("modelCd", form.getModelCd());
        }

        if (StringUtils.isNotBlank(form.getPhoneNo())) {
            condition.append(" AND clmu.telephone = :phoneNo ");
            params.put("phoneNo", form.getPhoneNo());
        }

        if (form.getStatus() != null && !form.getStatus().isEmpty()) {
            condition.append(" AND clmu.contact_status IN (:status) ");
            params.put("status", form.getStatus());
        }

        condition.append("  ORDER BY clmu.contact_date_from_customer, clmu.telephone ");

        return condition;
    }

    private StringBuilder query_sql() {

        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT clmu.contact_status             AS contactStatus                  ");
        sql.append("         , clmu.remark                     AS remark                         ");
        sql.append("         , clmu.lead_status                AS leadStatus                     ");
        sql.append("         , clmu.lead_type                  AS leadType                       ");
        sql.append("         , clmu.telephone                  AS phoneNo                        ");
        sql.append("         , clmu.customer_nm                AS customerNm                     ");
        sql.append("         , clmu.interested_model           AS interestedModel                ");
        sql.append("         , clmu.interested_color           AS interestedColor                ");
        sql.append("         , clmu.province                   AS provinceNm                     ");
        sql.append("         , clmu.district_town_city         AS cityNm                         ");
        sql.append("         , clmu.contact_date_from_customer AS contackDateFromCustomer        ");
        sql.append("         , clmu.call_date_by_dealer        AS dealerCallDate                 ");
        sql.append("         , clmu.estimate_time_to_buy       AS estimateTimeToBuy              ");
        sql.append("         , clmu.source                     AS dataSource                     ");
        sql.append("         , clmu.lead_management_result_id  AS leadManagementResultId         ");
        sql.append("         , ROW_NUMBER() OVER (ORDER BY clmu.lead_management_result_id) AS no ");

        return sql;
    }
}
