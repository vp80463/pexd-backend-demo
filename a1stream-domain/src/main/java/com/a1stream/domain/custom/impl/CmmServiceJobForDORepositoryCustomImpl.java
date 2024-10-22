package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.CategoryCd;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.master.CMM071701BO;
import com.a1stream.domain.custom.CmmServiceJobForDORepositoryCustom;
import com.a1stream.domain.form.master.CMM071701Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class CmmServiceJobForDORepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmServiceJobForDORepositoryCustom {

    @Override
    public List<CMM071701BO> findListByModelTypeCd(CMM071701Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT product_category_cd      AS modelTypeCd    ");
        sql.append("         , product_category_id      AS modelTypeId    ");
        sql.append("         , service_job_for_do_id    AS doId           ");
        sql.append("         , job_id                   AS jobId          ");
        sql.append("         , job_cd                   AS jobCd          ");
        sql.append("         , job_nm                   AS jobNm          ");
        sql.append("         , man_hours                AS laborHours     ");
        sql.append("         , labour_cost              AS laborCost      ");
        sql.append("         , total_cost               AS totalCost      ");
        sql.append("      FROM cmm_service_job_for_do                     ");
        sql.append("     WHERE site_id = :siteId                          ");

        if(form.getModelTypeId() != null) {

            if(form.getModelTypeCd().equals(CategoryCd.AT) || form.getModelTypeCd().equals(CategoryCd.MP)) {

                sql.append("   AND  product_category_cd = :modelTypeCd    ");
                params.put("modelTypeCd", form.getModelTypeCd());
            }

            if(form.getModelTypeCd().equals(CategoryCd.BIGBIKE)) {

                sql.append("   AND  product_category_cd <> :categoryCdAT  ");
                sql.append("   AND  product_category_cd <> :categoryCdMP  ");
                sql.append("   AND  product_category_cd <> :space         ");
                sql.append("   AND  product_category_cd <> :blank         ");
                params.put("categoryCdAT", CategoryCd.AT);
                params.put("categoryCdMP", CategoryCd.MP);
                params.put("space", CommonConstants.CHAR_SPACE);
                params.put("blank", CommonConstants.CHAR_BLANK);
            }
        }

        sql.append("  ORDER BY product_category_cd                        ");
        sql.append("         , job_cd                                     ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);

        return super.queryForList(sql.toString(), params, CMM071701BO.class);
    }

    @Override
    public ValueListResultBO findServiceJobByModelTypeList(ServiceJobVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT job_id AS id                           ");
        sql.append("        , job_cd AS code                         ");
        sql.append("        , job_nm AS name                         ");
        sql.append("        , concat(job_cd, ' ', job_nm)  AS desc   ");
        sql.append("        , man_hours AS manHours                  ");
        sql.append("        , total_cost AS stdRetailPrice           ");
        sql.append("    FROM cmm_service_job_for_do                  ");
        sql.append("   WHERE product_category_id = :modelType        ");
        sql.append("     AND service_category = :serviceCategoryId   ");

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND job_retrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        params.put("modelType", Long.valueOf(model.getModelType()));
        params.put("serviceCategoryId", model.getServiceCategoryId());

        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        if (pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<ServiceJobVLBO> result = super.queryForList(sql.toString(), params, ServiceJobVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public List<ServiceJobVLBO> findServiceJobByModelTypeListWithJobId(String serviceCatgeoryId, Long modelType, List<Long> jobIdList) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT job_id AS id                           ");
        sql.append("        , job_cd AS code                         ");
        sql.append("        , job_nm AS name                         ");
        sql.append("        , man_hours AS manHours                  ");
        sql.append("        , total_cost AS stdRetailPrice           ");
        sql.append("    FROM cmm_service_job_for_do                  ");
        sql.append("   WHERE product_category_id = :modelType        ");
        sql.append("     AND service_category = :serviceCategoryId   ");

        if (!jobIdList.isEmpty()) {

            sql.append(" AND job_id in (:jobIdList)   ");
            params.put("jobIdList", jobIdList);
        }

        params.put("modelType", modelType);
        params.put("serviceCategoryId", serviceCatgeoryId);

        return super.queryForList(sql.toString(), params, ServiceJobVLBO.class);
    }
}