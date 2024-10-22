package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.unit.SDM060101BO;
import com.a1stream.domain.custom.CmmSpecialCompanyTaxRepositoryCustom;
import com.a1stream.domain.form.unit.SDM060101Form;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Repository
public class CmmSpecialCompanyTaxRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmSpecialCompanyTaxRepositoryCustom {

    @Override
    public Page<SDM060101BO> pageCusTaxList(SDM060101Form model) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder query_sql = querySql();
        StringBuilder condition_sql = conditionSql(model, params);

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + condition_sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        if (!model.isPageFlg()) {
            pageable = Pageable.unpaged();
        }

        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(query_sql.append(condition_sql).toString(), params, SDM060101BO.class, pageable), pageable, count);
    }

    @Override
    public List<SDM060101BO> listCusTaxList(SDM060101Form model) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder query_sql = querySql();
        StringBuilder condition_sql = conditionSql(model, params);

        return super.queryForList(query_sql.append(condition_sql).toString(), params, SDM060101BO.class);
    }

    /**
     * @param model
     * @param params
     * @return
     */
    private StringBuilder conditionSql(SDM060101Form model, Map<String, Object> params) {

        StringBuilder condition_sql = new StringBuilder();

        condition_sql.append("     FROM cmm_special_company_tax csct                 ");
        condition_sql.append("    WHERE csct.site_id = :siteId                       ");

        params.put("siteId",CommonConstants.CHAR_DEFAULT_SITE_ID);

        if(StringUtils.isNotBlank(model.getCusTaxCode())){
            condition_sql.append(" AND csct.special_company_tax_cd = :cusTaxCode ");
            params.put("cusTaxCode", model.getCusTaxCode());
        }

        if(StringUtils.isNotBlank(model.getCusTaxName())){
            condition_sql.append(" AND csct.special_company_tax_nm like :cusTaxName ");
            params.put("cusTaxName", "%" + model.getCusTaxName() + "%");
        }

        if(StringUtils.isNotBlank(model.getAddress())){
            condition_sql.append(" AND csct.address like :address ");
            params.put("address", "%" + model.getAddress() + "%");
        }

        return condition_sql;
    }

    /**
     * @return
     */
    private StringBuilder querySql() {

        StringBuilder query_sql = new StringBuilder();

        query_sql.append("    SELECT        csct.special_company_tax_id as taxId      ");
        query_sql.append("                , csct.special_company_tax_cd as cusTaxCode ");
        query_sql.append("                , csct.special_company_tax_nm as cusTaxName ");
        query_sql.append("                , csct.address                as address    ");
        query_sql.append("                , csct.update_count           as updateCount");

        return query_sql;
    }
}
