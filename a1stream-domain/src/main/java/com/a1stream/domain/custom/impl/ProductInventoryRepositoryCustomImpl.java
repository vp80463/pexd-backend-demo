/**
 *
 */
package com.a1stream.domain.custom.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.bo.parts.SPM030101BO;
import com.a1stream.domain.bo.parts.SPM030501BO;
import com.a1stream.domain.bo.parts.SPM030801BO;
import com.a1stream.domain.bo.parts.SPQ030302BO;
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.custom.ProductInventoryRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030501Form;
import com.a1stream.domain.form.parts.SPQ030302Form;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

/**
 *
 */
public class ProductInventoryRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ProductInventoryRepositoryCustom {

    @Override
    public PageImpl<LocationVLBO> findLocationList(LocationVLForm model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append(" SELECT l.location_cd   as locationNo          ");
        selSql.append("      , l.location_type as locationTypeCd      ");
        selSql.append("      , w.workzone_id   as workzoneId          ");
        selSql.append("      , w.workzone_cd   as workzone            ");
        selSql.append("      , w.description   as workzoneDescription ");
        selSql.append("      , bt.bin_type_cd  as binType             ");
        selSql.append("      , bt.bin_type_id  as binTypeId           ");
        selSql.append("      , bt.description  as binTypeDescription  ");
        selSql.append("      , pi.primary_flag as mainLocation        ");
        selSql.append("      , l.location_id   as locationId          ");
        sql.append("   FROM product_inventory pi                   ");
        sql.append("  INNER JOIN location l                        ");
        sql.append("     ON pi.location_id = l.location_id         ");
        sql.append("    AND l.location_type in (:locationTypeList) ");
        sql.append("    AND pi.quantity >0                         ");
        sql.append("   LEFT JOIN workzone w                        ");
        sql.append("     ON l.workzone_id = w.workzone_id          ");
        sql.append("   LEFT JOIN bin_type bt                       ");
        sql.append("     ON l.bin_type_id = bt.bin_type_id         ");

        params.put("locationTypeList", model.getLocationTypeList());

        sql.append("  WHERE pi.facility_id = :pointId ");
        params.put("pointId", model.getPointId());

        sql.append("    AND pi.site_id = :siteId ");
        params.put("siteId", siteId);

        if(!ObjectUtils.isEmpty(model.getPartsId())){
            sql.append("    AND pi.product_id = :productId");
            params.put("productId", model.getPartsId());
        }
        if(!ObjectUtils.isEmpty(model.getWorkzone())){
            sql.append(" AND l.workzone_id = :workzone ");
            params.put("workzone", model.getWorkzone());
        }
        if(!ObjectUtils.isEmpty(model.getBinType())){
            sql.append(" AND l.bin_type_id = :binType ");
            params.put("binType", model.getBinType());
        }
        if(StringUtils.isNotBlank(model.getLocationCd())){
            sql.append(" AND l.location_cd = :locationCd ");
            params.put("locationCd", model.getLocationCd());
        }
        if(StringUtils.isNotBlank(model.getLocationType())){
            sql.append(" AND l.location_type = :locationType ");
            params.put("locationType", model.getLocationType());
        }
        if(StringUtils.isNotBlank(model.getMainLocationSign())){
            sql.append(" AND pi.primary_flag in (:flag)");
            if (StringUtils.equals(CommonConstants.CHAR_ALL, model.getMainLocationSign())) {
                List<String> flags = new ArrayList<>();
                flags.add(CommonConstants.CHAR_Y);
                flags.add(CommonConstants.CHAR_N);
                params.put("flag", flags);
            }
            if (StringUtils.equals(CommonConstants.CHAR_YES, model.getMainLocationSign())) {
                params.put("flag", CommonConstants.CHAR_Y);
            }
            if (StringUtils.equals(CommonConstants.CHAR_NO, model.getMainLocationSign())) {
                params.put("flag", CommonConstants.CHAR_N);
            }
        }

        sql.append(" ORDER BY l.location_cd ");

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT * " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(params);

        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, LocationVLBO.class, pageable), pageable, count);
    }

    @Override
    public List<PartsVLBO> findMainLocationIdList(List<Long> productIds, String siteId, Long facilityId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT prodi.product_id  AS id                  ");
        sql.append("          , prodi.location_id AS mainLocationId      ");
        sql.append("          , loc.location_cd   AS mainLocationCd      ");
        sql.append("       FROM product_inventory prodi                  ");
        sql.append(" INNER JOIN location loc                             ");
        sql.append("         ON loc.location_id = prodi.location_id      ");
        sql.append("      WHERE prodi.site_id = :siteId                  ");
        sql.append("        AND prodi.product_id IN (:productIds)        ");
        sql.append("        AND prodi.primary_flag = :primaryFlag        ");
        sql.append("        AND prodi.product_classification = :S001PART ");
        sql.append("        AND prodi.facility_id = :facilityId          ");

        params.put("siteId", siteId);
        params.put("productIds", productIds);
        params.put("primaryFlag", CommonConstants.CHAR_Y);
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("facilityId", facilityId);
        return super.queryForList(sql.toString(), params, PartsVLBO.class);
    }

    @Override
    public List<SPM030501BO> getPartsLocationList(SPM030501Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT l.location_cd     AS fromLocation        ");
        sql.append("          , pi.location_id    AS fromLocationId      ");
        sql.append("          , mci.code_data1    AS fromLocationType    ");
        sql.append("          , l.location_type   AS fromLocationTypeId  ");
        sql.append("          , pi.primary_flag   AS mainLocationSign    ");
        sql.append("          , pi.quantity       AS stockQty            ");
        sql.append("          , pi.product_inventory_id       AS productInventoryId            ");
        sql.append("       FROM product_inventory pi                     ");
        sql.append("  INNER JOIN location l                              ");
        sql.append("         ON pi.site_id     = l.site_id               ");
        sql.append("        AND pi.facility_id = l.facility_id           ");
        sql.append("        AND pi.location_id = l.location_id           ");
        sql.append("  LEFT JOIN mst_code_info mci                        ");
        sql.append("         ON mci.code_dbid  = l.location_type         ");
        sql.append("      WHERE pi.site_id     = :siteId                 ");
        sql.append("        AND pi.facility_id = :facilityId             ");
        sql.append("        AND pi.product_id  = :productId              ");

        params.put("siteId", siteId);
        params.put("facilityId", form.getPointId());
        params.put("productId", form.getPartsId());
        return super.queryForList(sql.toString(), params, SPM030501BO.class);
    }

    @Override
    public List<SPM030801BO> getStockOnlyLocationInfo(String siteId, Long facilityId, String costType, String productClassification){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT pi.location_id          as locationId         ");
        sql.append("           , pi.product_inventory_id as productInventoryId ");
        sql.append("           , pi.product_id           as productId          ");
        sql.append("           , w.workzone_id           as workzoneId         ");
        sql.append("           , pi.quantity             as qty                ");
        sql.append("           , pc.cost                 as cost               ");
        sql.append("        FROM product_inventory pi                          ");
        sql.append("  INNER JOIN location l                                    ");
        sql.append("          ON pi.location_id = l.location_id                ");
        sql.append("  INNER JOIN workzone w                                    ");
        sql.append("          ON w.workzone_id = l.workzone_id                 ");
        sql.append("  INNER JOIN mst_product mp                                ");
        sql.append("          ON mp.product_id = pi.product_id                 ");
        sql.append("   LEFT JOIN product_cost pc                               ");
        sql.append("          ON pc.site_id =:siteId                           ");
        sql.append("         AND pc.product_id = mp.product_id                 ");
        sql.append("         AND pc.cost_type =:costType                       ");
        sql.append("       WHERE pi.site_id =:siteId                           ");
        sql.append("         AND pi.facility_id =:facilityId                   ");
        sql.append("         AND pi.product_classification =:productClassification");
        sql.append("         AND pi.quantity  > 0                              ");
        sql.append("    ORDER BY w.description , l.location_cd , mp.product_cd ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("costType", costType);
        params.put("productClassification", productClassification);
        return super.queryForList(sql.toString(), params, SPM030801BO.class);
    }

    @Override
    public List<SPM030801BO> getAllLocationInfo(String siteId, Long facilityId, String productClassification){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT l.location_id            as locationId         ");
        sql.append("          , pi2.product_inventory_id as productInventoryId ");
        sql.append("          , pi2.product_id           as productId          ");
        sql.append("          , w.workzone_id            as workzoneId         ");
        sql.append("          , pi2.quantity             as qty                ");
        sql.append("       FROM location l                                     ");
        sql.append(" INNER JOIN workzone w                                     ");
        sql.append("         ON w.workzone_id = l.workzone_id                  ");
        sql.append("  LEFT JOIN product_inventory pi2                          ");
        sql.append("         ON pi2.location_id = l.location_id                ");
        sql.append("        AND pi2.product_classification =:productClassification");
        sql.append("        AND pi2.quantity > 0                               ");
        sql.append("        AND pi2.site_id = l.site_id                        ");
        sql.append("        AND pi2.facility_id = l.facility_id                ");
        sql.append("  LEFT JOIN mst_product mp                                 ");
        sql.append("         ON mp.product_id = pi2.product_id                 ");
        sql.append("      WHERE l.site_id  =:siteId                            ");
        sql.append("        AND l.facility_id =:facilityId                     ");
        sql.append("    ORDER BY w.description , l.location_cd , mp.product_cd ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", productClassification);
        return super.queryForList(sql.toString(), params, SPM030801BO.class);
    }

    @Override
    public Page<SPQ030302BO> getPartsStockDetailListPageable(SPQ030302Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("  SELECT mp.product_cd                                       AS partsNo              ");
        selSql.append("       , mp.sales_description                                AS partsNm              ");
        selSql.append("       , pi.product_id                                       AS partsId              ");
        selSql.append("       , COALESCE(mp2.product_cd, '')                        AS supersedingParts     ");
        selSql.append("       , mpr.from_product_id                                 AS supersedingPartsId   ");
        selSql.append("       , w.description                                       AS wz                   ");
        selSql.append("       , l.workzone_id                                       AS wzId                 ");
        selSql.append("       , l.location_cd                                       AS location             ");
        selSql.append("       , pi.location_id                                      AS locationId           ");
        selSql.append("       , bt.description                                      AS binType              ");
        selSql.append("       , l.bin_type_id                                       AS binTypeId            ");
        selSql.append("       , mci.code_data1                                      AS locationType         ");
        selSql.append("       , l.location_type                                     AS locationTypeId       ");
        selSql.append("       , pi.primary_flag                                     AS mainLocationSign     ");
        selSql.append("       , pi.quantity                                         AS stockQty             ");
        selSql.append("       , SPLIT_PART(mp.all_nm, '|', 1)                       AS largeGroup           ");
        selSql.append("       , SPLIT_PART(mp.all_nm, '|', 2)                       AS middleGroup          ");
        sql.append("       FROM product_inventory pi                                                        ");
        sql.append("  LEFT JOIN mst_product mp                                                              ");
        sql.append("         ON mp.product_id = pi.product_id                                               ");
        sql.append("  LEFT JOIN mst_product_relation mpr                                                    ");
        sql.append("         ON mpr.to_product_id = pi.product_id                                           ");
        sql.append("        AND mpr.site_id = mp.site_id                                                    ");
        sql.append("  LEFT JOIN mst_product mp2                                                             ");
        sql.append("         ON mp2.product_id = mpr.from_product_id                                        ");
        sql.append(" INNER JOIN location l                                                                  ");
        sql.append("         ON l.location_id = pi.location_id                                              ");
        sql.append("        AND l.facility_id  = pi.facility_id                                             ");
        sql.append("        AND l.site_id = :siteId                                                         ");
        sql.append("  LEFT JOIN workzone w                                                                  ");
        sql.append("         ON w.workzone_id = l.workzone_id                                               ");
        sql.append("        AND w.site_id = :siteId                                                         ");
        sql.append("  LEFT JOIN bin_type bt                                                                 ");
        sql.append("         ON bt.bin_type_id = l.bin_type_id                                              ");
        sql.append("        AND bt.site_id = :siteId                                                        ");
        sql.append("  LEFT JOIN mst_code_info mci                                                           ");
        sql.append("         ON mci.code_dbid = l.location_type                                             ");
        sql.append("      WHERE pi.facility_id = :facilityId                                                ");

        params.put("siteId", siteId);
        params.put("facilityId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND pi.product_id = :productId ");
            params.put("productId", form.getPartsId());
        }
        if (form.getProductCategory() != null && !form.getProductCategory().isEmpty()) {
            if (form.getProductCategory().size() == 1) {
                sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                params.put("largeGroupId", form.getProductCategory().get(0));
            } else if(form.getProductCategory().size() == 2) {
                sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                sql.append(" AND split_part(mp.all_parent_id, '|', 2) = :middleGroupId ");
                params.put("largeGroupId", form.getProductCategory().get(0));
                params.put("middleGroupId", form.getProductCategory().get(1));
            }
        }
        if (StringUtils.isNotBlank(form.getLocationId())) {
            sql.append(" AND pi.location_id = :locationId ");
            params.put("locationId", Long.valueOf(form.getLocationId()));
        }
        if (StringUtils.isNotBlank(form.getLocationTypeId())) {
            sql.append(" AND l.location_type = :locationType ");
            params.put("locationType", form.getLocationTypeId());
        }
        if (StringUtils.isNotBlank(form.getMainLocationSign())) {
            if (StringUtils.equals(CommonConstants.CHAR_Y, form.getMainLocationSign())) {
                sql.append(" AND pi.primary_flag = :Y ");
                params.put("Y", CommonConstants.CHAR_Y);
            }
            if (StringUtils.equals(CommonConstants.CHAR_N, form.getMainLocationSign())) {
                sql.append(" AND pi.primary_flag IN (:primaryFlagList) ");
                List<String> primaryFlagList = Arrays.asList(CommonConstants.CHAR_N, CommonConstants.CHAR_Y);
                params.put("primaryFlagList", primaryFlagList);
            }
        }
        sql.append(" ORDER BY mp.product_cd, l.location_cd ");
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ030302BO.class, pageable), pageable, count);
    }

    @Override
    public List<SPM030101BO> getProductInventoryList(String siteId, Long facilityId, Set<Long> productIdSet, String productClassification, String primaryFlag,String locationType) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT pi.location_id   AS locationId                   ");
        sql.append("          , pi.product_id    AS productId                    ");
        sql.append("          , l.location_cd    AS locationCd                   ");
        sql.append("          , pi.quantity      AS qty                          ");
        sql.append("          , l.location_type  AS locationType                 ");
        sql.append("       FROM product_inventory pi                             ");
        sql.append("  LEFT JOIN location l                                       ");
        sql.append("         ON pi.location_id = l.location_id                   ");
        sql.append("    WHERE pi.site_id = :siteId                               ");
        sql.append("      AND pi.facility_id = :facilityId                       ");
        sql.append("      AND pi.product_id IN (:productIdSet)                   ");
        sql.append("      AND pi.product_classification = :productClassification ");
        sql.append("      AND pi.primary_flag = :primaryFlag                     ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productIdSet", productIdSet);
        params.put("productClassification", productClassification);
        params.put("primaryFlag", primaryFlag);
        if(StringUtils.isNotBlank(locationType)){
            sql.append(" AND l.location_type = :locationType ");
            params.put("locationType", locationType);
        }
        return super.queryForList(sql.toString(), params, SPM030101BO.class);
    }

    /**
    * 功能描述:DIM0204画面，查询后用来删除库存库位表数据
    *
    * @author mid2178
    */
    @Override
    public List<ProductInventoryVO> getProInventoryToDel(String siteId, Long facilityId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" select * from product_inventory     ");
        sql.append("  where site_id =:siteId             ");
        sql.append("    and facility_id  =:facilityId    ");
        sql.append("    and exists                       ");
        sql.append("       (select 1 from mst_product    ");
        sql.append("         where mst_product.site_id in (:cmmSiteId,:siteId)                  ");
        sql.append("           and mst_product.product_id = product_inventory.product_id        ");
        sql.append("           and mst_product.product_classification =:productClassification)  ");

        params.put("siteId", siteId);
        params.put("cmmSiteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());

        return super.queryForList(sql.toString(), params, ProductInventoryVO.class);
    }

    /**
    * 功能描述:spq0308查出在使用的库位
    *
    * @author mid2178
    */
    @Override
    public Map<String, SPQ030801BO> getLocationInUse(String siteId, Long pointId, Long binTypeId, String locationTypeId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Map<String, SPQ030801BO> returnMap = new HashMap<>();

        sql.append("     select mci.code_data1                  as locationType  ");
        sql.append("          , bt.description       as binType       ");
//        sql.append("          , bt.bin_type_cd                  as binType       ");
        sql.append("          , count(distinct pi2.location_id) as inUseQty      ");
        sql.append("       from product_inventory pi2                            ");
        sql.append(" inner join location l                                       ");
        sql.append("         on l.location_id = pi2.location_id                  ");
        sql.append("        and l.facility_id = pi2.facility_id                  ");

        if(binTypeId != null) {
            sql.append("   and l.bin_type_id =:binTypeId        ");
            params.put("binTypeId", binTypeId);
        }
        if(StringUtils.isNotBlank(locationTypeId)) {
            sql.append("   and l.location_type =:locationTypeId ");
            params.put("locationTypeId", locationTypeId);
        }

        sql.append("  inner join mst_code_info mci              ");
        sql.append("          on mci.code_id =:codeId           ");
        sql.append("         and mci.site_id =:cmmSiteId        ");
        sql.append("         and mci.code_dbid = l.location_type");
        sql.append("  inner join bin_type bt                    ");
        sql.append("          on bt.bin_type_id = l.bin_type_id ");
        sql.append("      where pi2.site_id =:siteId            ");
        sql.append("        and pi2.quantity > 0                ");
        sql.append("       and pi2.facility_id =:pointId        ");
        sql.append("   group by mci.code_data1, bt.description  ");

        params.put("siteId",siteId);
        params.put("pointId",pointId);
        params.put("cmmSiteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("codeId",PJConstants.LocationType.CODE_ID);

        List<SPQ030801BO> spq0308Bos = super.queryForList(sql.toString(), params, SPQ030801BO.class);
        for (SPQ030801BO BO : spq0308Bos) {
            returnMap.put(BO.getLocationType() + CommonConstants.CHAR_VERTICAL_BAR + BO.getBinType(), BO);
        }

        return returnMap;
    }

    /**
    * 功能描述:dim0207根据cds查db中数据
    *
    * @author mid2178
    */
    @Override
    public Map<String, ProductInventoryVO> getProInvByCds(Set<String> locationCds, Set<String> productCds, Long facilityId, String siteId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Map<String, ProductInventoryVO> returnMap = new HashMap<>();

        sql.append("     select pi2.*                              ");
        sql.append("       from product_inventory pi2              ");
        sql.append(" inner join location l                         ");
        sql.append("         on l.location_id = pi2.location_id    ");
        sql.append("        and l.location_cd in(:locationCds)     ");
        sql.append(" inner join mst_product mp                     ");
        sql.append("         on mp.product_id = pi2.product_id     ");
        sql.append("        and mp.product_cd in(:productCds)      ");
        sql.append("      where pi2.site_id =:siteId               ");
        sql.append("        and pi2.facility_id =:facilityId       ");

        params.put("locationCds", locationCds);
        params.put("productCds", productCds);
        params.put("facilityId", facilityId);
        params.put("siteId", siteId);

        List<ProductInventoryVO> vos = super.queryForList(sql.toString(), params, ProductInventoryVO.class);
        for (ProductInventoryVO vo : vos) {
            returnMap.put(vo.getLocationId().toString()
                        + CommonConstants.CHAR_VERTICAL_BAR
                        + vo.getProductId().toString()
                        , vo);
        }

        return returnMap;
    }
}
