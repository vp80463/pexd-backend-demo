package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.custom.MstOrganizationRepositoryCustom;
import com.a1stream.domain.vo.MstOrganizationVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class MstOrganizationRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstOrganizationRepositoryCustom {

	@Override
    public List<CmmHelperBO> getSupplierList(String siteId, BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = supplier_list_sql(siteId, model, params);

        List<CmmHelperBO> result = super.queryForList(sql.toString(), params, CmmHelperBO.class);

        return result;
    }
	
    @Override
    public ValueListResultBO findSupplierList(String siteId, BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = supplier_list_sql(siteId, model, params);

        // pop-up
        String countSql = "SELECT COUNT(1) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);

        // pop-over
        if ( pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue ");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public MstOrganizationVO getPartSupplier(String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mo.organization_id AS organizationId                ");
        sql.append("          , mo.organization_cd AS organizationCd                ");
        sql.append("          , mo.organization_nm AS organizationNm                ");
        sql.append("       FROM mst_organization mo                                 ");
        sql.append("          , organization_relation or2                           ");
        sql.append("      WHERE mo.site_id = :siteId                                ");
        sql.append("        AND mo.site_id = or2.site_id                            ");
        sql.append("        AND or2.product_classification = :productClassification ");
        sql.append("        AND or2.relation_type = :relationTypeId                 ");
        sql.append("        AND or2.to_organization_id = mo.organization_id         ");
        sql.append("        AND or2.default_flag = :defaultFlag                     ");

        params.put("siteId", siteId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("relationTypeId", OrgRelationType.SUPPLIER.getCodeDbid());
        params.put("defaultFlag", CommonConstants.CHAR_Y);

        return super.queryForSingle(sql.toString(), params, MstOrganizationVO.class);
    }

    @Override
    public List<MstOrganizationVO> getPartSupplierList(Set<String> siteIdSet) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mo.organization_id AS organizationId                ");
        sql.append("          , mo.organization_cd AS organizationCd                ");
        sql.append("          , mo.organization_nm AS organizationNm                ");
        sql.append("          , mo.site_id         AS siteId                        ");
        sql.append("       FROM mst_organization mo                                 ");
        sql.append("          , organization_relation or2                           ");
        sql.append("      WHERE mo.site_id IN (:siteIdSet)                          ");
        sql.append("        AND mo.site_id = or2.site_id                            ");
        sql.append("        AND or2.product_classification = :productClassification ");
        sql.append("        AND or2.relation_type = :relationTypeId                 ");
        sql.append("        AND or2.to_organization_id = mo.organization_id         ");
        sql.append("        AND or2.default_flag = :defaultFlag                     ");

        params.put("siteIdSet", siteIdSet);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("relationTypeId", OrgRelationType.SUPPLIER.getCodeDbid());
        params.put("defaultFlag", CommonConstants.CHAR_Y);

        return super.queryForList(sql.toString(), params, MstOrganizationVO.class);
    }

	private StringBuilder supplier_list_sql(String siteId, BaseVLForm model, Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mo.organization_id AS id                            ");
        sql.append("          , mo.organization_cd AS code                          ");
        sql.append("          , mo.organization_nm AS name                          ");
        sql.append("          , concat(mo.organization_cd, ' ', mo.organization_nm) AS desc                    ");
        sql.append("       FROM mst_organization mo                                 ");
        sql.append("          , organization_relation or2                           ");
        sql.append("      WHERE or2.site_id = :siteId                               ");
        sql.append("        AND or2.relation_type = :relationType                   ");
        sql.append("        AND or2.site_id = mo.site_id                            ");
        sql.append("        AND or2.to_organization_id = mo.organization_id         ");

        params.put("siteId", siteId);
        params.put("relationType", PJConstants.OrgRelationType.SUPPLIER.getCodeDbid());

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND mo.organization_retrieve LIKE :content ");

            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY mo.organization_cd ");
        
		return sql;
	}
}
