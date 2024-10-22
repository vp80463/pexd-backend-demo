package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.FacilityRoleType;
import com.a1stream.common.constants.PJConstants.FacilityType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.CmmHelperBO;
import com.a1stream.common.model.HomePageForm;
import com.a1stream.common.model.HomePagePointBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.bo.unit.SDM010901BO;
import com.a1stream.domain.custom.MstFacilityRepositoryCustom;
import com.a1stream.domain.vo.MstFacilityVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class MstFacilityRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstFacilityRepositoryCustom {

	@Override
    public List<CmmHelperBO> getPointList(PJUserDetails uc, BaseVLForm model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = query_point_sql(uc, model, params);

        List<CmmHelperBO> result = super.queryForList(sql.toString(), params, CmmHelperBO.class);

        return result;
    }

    @Override
    public ValueListResultBO findPointList(PJUserDetails uc, BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = query_point_sql(uc, model, params);

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
    public List<CMM020501BO> findPointListBySiteId(String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT facility_id        AS pointId          ");
        sql.append("        , facility_cd        AS pointCd          ");
        sql.append("        , facility_nm        AS pointNm          ");
        sql.append("        , sp_purchase_flag   AS wsDealerSign     ");
        sql.append("        , CASE facility_role_type                ");
        sql.append("            WHEN :roleTypeShop THEN :charY       ");
        sql.append("            ELSE :charN                          ");
        sql.append("          END                AS shop             ");
        sql.append("        , city_nm            AS area             ");
        sql.append("        , contact_tel        AS telephone        ");
        sql.append("        , from_date          AS effectiveDate    ");
        sql.append("        , to_date            AS expiredDate      ");
        sql.append("     FROM mst_facility                           ");
        sql.append("    WHERE site_id = :siteId                      ");
        sql.append(" ORDER BY facility_cd                            ");

        params.put("siteId", siteId);
        params.put("roleTypeShop", FacilityRoleType.KEY_SHOP);
        params.put("charY", CommonConstants.CHAR_Y);
        params.put("charN", CommonConstants.CHAR_N);

        return super.queryForList(sql.toString(), params, CMM020501BO.class);
    }

    @Override
    public List<PartsDeadStockItemBO> getAllNoneReturnWarehousePoints(String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Set<String> statusList = Set.of(ReturnRequestStatus.RECOMMENDED.getCodeDbid()
                                        , ReturnRequestStatus.REQUESTED.getCodeDbid()
                                        , ReturnRequestStatus.APPROVED.getCodeDbid()
                                        , ReturnRequestStatus.ONPICKING.getCodeDbid());

        sql.append("     select fi.facility_id as facilityId            ");
        sql.append("          , fi.facility_cd as facilityCd            ");
        sql.append("       from mst_facility fi                         ");
        sql.append("      where                                         ");
        sql.append("            not exists(              ");
        sql.append("                select 1 from return_request_list rrl where rrl.request_status in (:statusList) and rrl.site_id=fi.site_id       ");
        sql.append("                      )              ");
        sql.append(" and fi.site_id=:siteId");

        params.put("siteId",siteId);
        params.put("statusList",statusList);

        return super.queryForList(sql.toString(), params, PartsDeadStockItemBO.class);
    }

    @Override
    public List<MstFacilityVO> getFacilityByDateList(String siteId,String collectDate) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sql.append("    select  *     ");
        sql.append("      from  mst_facility                    ");
        sql.append("     where  site_id =:siteId                ");
        sql.append("       and  from_date <=:collectDate        ");
        sql.append("       and  to_date >=:collectDate          ");
        sql.append("       and  (delete_flag is null or delete_flag != :flag )    ");

        param.put("siteId", siteId);
        param.put("collectDate", collectDate);
        param.put("flag", CommonConstants.CHAR_Y);

        return super.queryForList(sql.toString(), param, MstFacilityVO.class);

    }

    @Override
    public List<HomePagePointBO> findPointListForHome(HomePageForm form) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("     SELECT mf.facility_id AS pointId    ");
        sql.append("          , mf.facility_cd AS pointCd    ");
        sql.append("          , mf.facility_nm AS pointNm    ");
        sql.append("       FROM mst_facility mf              ");
        sql.append("      WHERE mf.site_id = :siteId         ");

        param.put("siteId", form.getSiteId());

        //不是管理员登录时，加上权限控制
        if (!StringUtils.equals(form.getBaseSiteId(), CommonConstants.CHAR_DEFAULT_SITE_ID)
               && !StringUtils.equals(form.getBaseSiteId(), CommonConstants.CHAR_YMSLX_SITE_ID)) {

            sql.append(" AND EXISTS ( SELECT 1                                ");
            sql.append("                FROM cmm_person_facility cpf          ");
            sql.append("               WHERE cpf.facility_id = mf.facility_id ");
            sql.append("                 AND cpf.person_id = :personId  )     ");

            param.put("personId", form.getPersonId());
        }

        sql.append("   ORDER BY mf.facility_cd                   ");

        param.put("siteId", form.getSiteId());

        return super.queryForList(sql.toString(), param, HomePagePointBO.class);
    }

	private StringBuilder query_point_sql(PJUserDetails uc, BaseVLForm model, Map<String, Object> params) {

		StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_id AS id         ");
        sql.append("          , mf.facility_cd AS code       ");
        sql.append("          , mf.facility_nm AS name       ");
        sql.append("          , concat(mf.facility_cd, ' ', mf.facility_nm)  as desc ");
        sql.append("       FROM mst_facility mf              ");

        sql.append("      WHERE 1 = 1         ");

        switch(model.getArg0()) {
        	case FacilityType.WO:
	        	sql.append(" AND mf.sp_purchase_flag = :wo ");
	            sql.append(" AND mf.site_id = :siteId");
	            params.put("wo", CommonConstants.CHAR_SUPPER_ADMIN_FLAG);
	            params.put("siteId", uc.getDealerCode());
	            break;
        	case FacilityType.SHOP:
        		sql.append(" AND mf.site_id = :siteId");
                sql.append(" AND EXISTS ( SELECT 1                                ");
                sql.append("                FROM cmm_person_facility cpf          ");
                sql.append("               WHERE cpf.facility_id = mf.facility_id ");
                sql.append("                 AND cpf.person_id = :personId  )     ");
                params.put("siteId", uc.getDealerCode());
                params.put("personId", uc.getPersonId());
                break;
        	case FacilityType.SHOP_ALL:
        		sql.append(" AND mf.site_id = :siteId");
                params.put("siteId", uc.getDealerCode());
                break;
        	case FacilityType.CONSIGNEE:
        		sql.append(" AND mf.site_id <> :siteId");
                params.put("siteId", uc.getDealerCode());
                break;
        }

        if (StringUtils.equals(model.getArg1(), CommonConstants.CHAR_SUPPER_ADMIN_FLAG)) {

            sql.append(" AND mf.facility_id = :pointId ");
            params.put("pointId", uc.getDefaultPointId());
        }

        if (StringUtils.isNotBlank(model.getContent())) {

            sql.append(" AND mf.facility_retrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append(" ORDER BY mf.facility_cd ");

		return sql;
	}

    @Override
    public List<SDM010901BO> getPointList() {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();
        sql.append("     SELECT DISTINCT mf.facility_id AS id                                ");
        sql.append("                   , mf.facility_cd AS code                              ");
        sql.append("                   , mf.facility_nm AS name                              ");
        sql.append("                   , CONCAT(mf.facility_cd, ' ', mf.facility_nm) AS desc ");
        sql.append("                FROM mst_facility mf                                     ");
        sql.append("                   , receipt_manifest rm                                 ");
        sql.append("               WHERE rm.to_facility_id = mf.facility_id                  ");

        return super.queryForList(sql.toString(), param, SDM010901BO.class);

    }
}
