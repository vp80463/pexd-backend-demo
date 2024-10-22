package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.CmmServiceGroupItemRepositoryCustom;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class CmmServiceGroupItemRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmServiceGroupItemRepositoryCustom {

    @Override
    public ValueListResultBO findServiceJobByModelList(ServiceJobVLForm model, Set<String> modelCode) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" WITH service_group_list AS (                                 ");
        sql.append("     SELECT service_group_id                                  ");
        sql.append("          , prod_cd                                           ");
        sql.append("       FROM cmm_service_group_item                            ");
        sql.append("      WHERE site_id = :siteId                                 ");
        sql.append("    AND prod_cd IN (:splitedModelCodes)                       ");
        sql.append(" ),service_job_list as (                                      ");
        sql.append("     SELECT mp.product_id          AS id                      ");
        sql.append("          , mp.product_cd          AS code                    ");
        sql.append("          , mp.sales_description   AS name                    ");
        sql.append("          , concat(mp.product_cd, ' ', mp.sales_description) AS desc ");
        sql.append("          , csgjm.man_hours        AS manHours                ");
        sql.append("          , sgl.prod_cd            AS prod_cd                 ");
        sql.append("          , row_number() OVER (PARTITION BY mp.product_cd ORDER BY CASE WHEN prod_cd = 'ALL' THEN 1 ELSE 0 END, prod_cd) AS rn ");
        sql.append("    FROM service_group_list sgl                               ");
        sql.append("       , cmm_service_group_job_manhour csgjm                  ");
        sql.append("       , mst_product mp                                       ");
        sql.append("   WHERE sgl.service_group_id      = csgjm.service_group_id   ");
        sql.append("     AND csgjm.site_id             = :siteId                  ");
        sql.append("     AND csgjm.site_id             = mp.site_id               ");
        sql.append("     AND mp.product_id             = csgjm.cmm_service_job_id ");
        sql.append("     AND mp.product_classification = :pc                      ");

        if (StringUtils.isNotBlank(model.getServiceJobCd())) {
            sql.append("AND mp.product_cd = :serviceJobCd ");
            params.put("serviceJobCd", model.getServiceJobCd());
        }

        if (StringUtils.isNotBlank(model.getServiceJobNm())) {
            sql.append("AND mp.sales_description = :serviceJobNm ");
            params.put("serviceJobNm", model.getServiceJobNm());
        }

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND REPLACE(upper(mp.product_retrieve), ' ', '') LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("     )                                                        ");
        sql.append("   SELECT id,code,name,\"desc\",manHours                          ");
        sql.append("     FROM service_job_list                                    ");
        sql.append("    WHERE rn = '1'                                            ");
        sql.append(" ORDER BY code                                                ");

        modelCode.add(CommonConstants.CHAR_ALL);

        params.put("splitedModelCodes", modelCode);

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("pc", ProductClsType.SERVICE.getCodeDbid());

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

        List<ServiceJobVLBO> result = super.queryForList(sql.toString(), params, ServiceJobVLBO.class);

        return new ValueListResultBO(result, count);
    }

    /**
     * @author mid1341
     */
    @Override
    public List<ServiceJobVLBO> findJobListByModel(List<String> modelCdList, List<String> jobCdList, List<Long> jobIdList) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  WITH service_group_list AS (                                     ");
        sql.append("        SELECT service_group_id                                    ");
        sql.append("          FROM cmm_service_group_item                              ");
        sql.append("         WHERE prod_cd in (:modelCdList)                           ");
        sql.append("         )                                                         ");
        sql.append("   SELECT mp.product_id          AS id                             ");
        sql.append("        , mp.product_cd          AS code                           ");
        sql.append("        , mp.sales_description   AS name                           ");
        sql.append("        , concat(mp.product_cd, ' ', mp.sales_description) AS desc ");
        sql.append("        , csgjm.man_hours        AS manHours                       ");
        sql.append("     FROM service_group_list sgl                                   ");
        sql.append("        , cmm_service_group_job_manhour csgjm                      ");
        sql.append("        , mst_product mp                                           ");
        sql.append("    WHERE sgl.service_group_id = csgjm.service_group_id            ");
        sql.append("      AND mp.product_id = csgjm.cmm_service_job_id                 ");
        sql.append("      AND mp.product_classification = :proCategory                 ");

        if (!jobCdList.isEmpty()) {
            sql.append("      AND mp.product_cd in (:jobCdList)                            ");
            params.put("jobCdList", jobCdList);
        }

        if (!jobIdList.isEmpty()) {
            sql.append("      AND mp.product_id in (:jobIdList)                            ");
            params.put("jobIdList", jobIdList);
        }

        sql.append(" GROUP BY mp.product_id,  csgjm.man_hours ");

        params.put("modelCdList", modelCdList);

        params.put("proCategory", ProductClsType.SERVICE.getCodeDbid());

        return super.queryForList(sql.toString(), params, ServiceJobVLBO.class);
    }
}
