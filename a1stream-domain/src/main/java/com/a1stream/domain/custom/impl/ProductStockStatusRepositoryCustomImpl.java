package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SdStockStatus;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.domain.bo.parts.SPQ030301BO;
import com.a1stream.domain.bo.parts.SPQ030501BO;
import com.a1stream.domain.bo.parts.SPQ030501PrintDetailBO;
import com.a1stream.domain.bo.unit.SDQ011301BO;
import com.a1stream.domain.custom.ProductStockStatusRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ030301Form;
import com.a1stream.domain.form.parts.SPQ030501Form;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;
import com.ymsl.solid.web.usercontext.UserDetailsAccessor;

public class ProductStockStatusRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ProductStockStatusRepositoryCustom {

    @Override
    public Page<SPQ030301BO> getPartsStockListPageable(SPQ030301Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> maps = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        StringBuilder selSql = new StringBuilder();

        selSql.append("    SELECT mp.product_cd                                                                                AS partsNo             ");
        selSql.append("         , mp.sales_description                                                                         AS partsNm             ");
        selSql.append("         , pss.product_id                                                                               AS partsId             ");
        selSql.append("         , COALESCE(mp2.product_cd, '')                                                                 AS supersedingParts    ");
        selSql.append("         , mpr.from_product_id                                                                          AS supersedingPartsId  ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONHANDQTY        THEN pss.quantity ELSE 0 END) AS onHandQty      ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ALLOCATEDQTY     THEN pss.quantity ELSE 0 END) AS allocatedQty   ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018BOQTY            THEN pss.quantity ELSE 0 END) AS boQty          ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONRECEIVINGQTY   THEN pss.quantity ELSE 0 END) AS onReceivingQty ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONPICKINGQTY     THEN pss.quantity ELSE 0 END) AS onPickingQty   ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018HOONPURCHASEQTY  THEN pss.quantity ELSE 0 END) AS hoQty          ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018EOONPURCHASEQTY  THEN pss.quantity ELSE 0 END) AS eoQty          ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ROONPURCHASEQTY  THEN pss.quantity ELSE 0 END) AS roQty          ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018WOONPURCHASEQTY  THEN pss.quantity ELSE 0 END) AS woQty          ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONTRANSFERINQTY  THEN pss.quantity ELSE 0 END) AS onTransferQty  ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONSERVICEQTY     THEN pss.quantity ELSE 0 END) AS onServiceQty   ");
        selSql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONFROZENQTY      THEN pss.quantity ELSE 0 END) AS onFrozenQty    ");
        selSql.append("         , COALESCE(pc.cost, 0)                                                                              AS averageCost    ");
        selSql.append("         , SPLIT_PART(mp.all_nm, '|', 1)                                                                     AS largeGroup     ");
        selSql.append("         , SPLIT_PART(mp.all_nm, '|', 2)                                                                     AS middleGroup    ");
        sql.append("         FROM product_stock_status pss                                                                                            ");
        sql.append("    LEFT JOIN mst_product mp                                                                                                      ");
        sql.append("           ON mp.product_id   = pss.product_id                                                                                    ");
        sql.append("    LEFT JOIN mst_product_relation mpr                                                                                            ");
        sql.append("           ON mpr.to_product_id = pss.product_id                                                                                  ");
        sql.append("          AND mpr.site_id       = mp.site_id                                                                                      ");
        sql.append("    LEFT JOIN mst_product mp2                                                                                                     ");
        sql.append("           ON mp2.product_id    = mpr.from_product_id                                                                             ");
        sql.append("    LEFT JOIN product_cost pc                                                                                                     ");
        sql.append("           ON pc.site_id    = pss.site_id                                                                                         ");
        sql.append("          AND pc.product_id = pss.product_id                                                                                      ");
        sql.append("          AND pc.cost_type = :S011AVERAGECOST                                                                                     ");
        sql.append("        WHERE pss.site_id     = :siteId                                                                                           ");
        sql.append("          AND pss.facility_id = :facilityId                                                                                       ");

        params.put("siteId", siteId);
        params.put("facilityId", form.getPointId());
        params.put("S011AVERAGECOST", CostType.AVERAGE_COST);

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND mp.product_id = :productId ");
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
        maps.put("S018ONHANDQTY", SpStockStatus.ONHAND_QTY.getCodeDbid());
        maps.put("S018ALLOCATEDQTY", SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        maps.put("S018BOQTY", SpStockStatus.BO_QTY.getCodeDbid());
        maps.put("S018ONRECEIVINGQTY", SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        maps.put("S018ONPICKINGQTY", SpStockStatus.ONPICKING_QTY.getCodeDbid());
        maps.put("S018HOONPURCHASEQTY", SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        maps.put("S018EOONPURCHASEQTY", SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        maps.put("S018ROONPURCHASEQTY", SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        maps.put("S018WOONPURCHASEQTY", SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid());
        maps.put("S018ONTRANSFERINQTY", SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        maps.put("S018ONSERVICEQTY", SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        maps.put("S018ONFROZENQTY", SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        sql.append("  GROUP BY mp.product_cd, mp.sales_description, pss.product_id, mp2.product_cd, mpr.from_product_id, pc.cost_type, pc.cost, mp.all_nm ");
        sql.append(" ORDER BY mp.product_cd ");
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        params.putAll(maps);
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ030301BO.class, pageable), pageable, count);
    }

    @Override
    public List<PartsVLBO> findOnHandQtyList(List<Long> productIds, String siteId, Long facilityId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT product_id    AS id                            ");
        sql.append("      , SUM(quantity) AS onHandQty                     ");
        sql.append("   FROM product_stock_status                           ");
        sql.append("  WHERE site_id = :siteId                              ");
        sql.append("    AND product_stock_status_type = :S018ONHANDQTY     ");
        sql.append("    AND product_classification = :S001PART             ");
        sql.append("    AND facility_id = :facilityId                      ");

        if(productIds != null && !productIds.isEmpty()) {
        	sql.append("    AND product_id in :productIds                  ");
        	params.put("productIds", productIds);
        }

        params.put("S018ONHANDQTY", PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("siteId", siteId);
        params.put("S001PART", ProductClsType.PART.getCodeDbid());
        params.put("facilityId", facilityId);
        sql.append(" GROUP BY product_id                                   ");
        return super.queryForList(sql.toString(), params, PartsVLBO.class);
    }

    @Override
    public List<SPQ030501BO> getPartsOnWorkingCheckList(SPQ030501Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mp.product_cd                                                                                AS partsNo               ");
        sql.append("         , mp.sales_description                                                                         AS partsNm               ");
        sql.append("         , pss.product_id                                                                               AS partsId               ");
        sql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONRECEIVINGQTY   THEN pss.quantity ELSE 0 END ) AS onReceivingQty  ");
        sql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONPICKINGQTY     THEN pss.quantity ELSE 0 END ) AS onPickingQty    ");
        sql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONFROZENQTY      THEN pss.quantity ELSE 0 END ) AS onFrozenQty     ");
        sql.append("         , SUM(CASE WHEN pss.product_stock_status_type = :S018ONTRANSFERINQTY  THEN pss.quantity ELSE 0 END ) AS onTransferInQty ");
        sql.append("      FROM product_stock_status pss                                                                                              ");
        sql.append(" LEFT JOIN mst_product mp                                                                                                        ");
        sql.append("        ON mp.product_id   = pss.product_id                                                                                      ");
        sql.append("     WHERE pss.site_id     = :siteId                                                                                             ");
        sql.append("       AND pss.facility_id = :facilityId                                                                                         ");

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND pss.product_id = :productId ");
            params.put("productId", form.getPartsId());
        }

        params.put("siteId", siteId);
        params.put("facilityId", form.getPointId());

        params.put("S018ONRECEIVINGQTY", SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        params.put("S018ONPICKINGQTY", SpStockStatus.ONPICKING_QTY.getCodeDbid());
        params.put("S018ONFROZENQTY", SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("S018ONTRANSFERINQTY", SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        sql.append(" GROUP BY mp.product_cd, mp.sales_description, pss.product_id ");
        sql.append(" HAVING (SUM(case when pss.product_stock_status_type = :S018ONRECEIVINGQTY then pss.quantity else 0 end )>0 ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONPICKINGQTY then pss.quantity else 0 end )>0    ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONFROZENQTY then pss.quantity else 0 end )>0     ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONTRANSFERINQTY then pss.quantity else 0 end )>0)");
        sql.append(" ORDER BY mp.product_cd ");
        return super.queryForList(sql.toString(), params, SPQ030501BO.class);
    }

    /**
     * Cai Yangmei 20240606
     */
    @Override
    public void updateStockStatusQuantitySubForStockList(List<ProductStockStatusVO> stockStatusVOList, Set<Long> productStockStatusIds) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("  UPDATE product_stock_status   ");
        sql.append("     SET quantity = quantity -  ");
        sql.append("         CASE product_id        ");

        for (ProductStockStatusVO productStockStatusVO : stockStatusVOList){
            sql.append(" WHEN ").append(productStockStatusVO.getProductId()).append(" THEN ").append(productStockStatusVO.getQuantity());
        }

        sql.append("         END                            ");
        sql.append("       , update_count = update_count + 1                 ");
        sql.append("       , update_program = :updateProgram                 ");
        sql.append("       , last_updated = :currentTime                     ");
        sql.append("       , last_updated_by = :userCd                     ");
        sql.append("   WHERE product_stock_status_id in (:productStockStatusIds) ");

        param.put("updateProgram", stockStatusVOList.iterator().next().getUpdateProgram());
        param.put("productStockStatusIds", productStockStatusIds);
        param.put("currentTime", DateUtils.getCurrentDate());
        param.put("userCd", Objects.isNull(UserDetailsAccessor.DEFAULT.get()) ? stockStatusVOList.iterator().next().getLastUpdatedBy() : ((PJUserDetails)UserDetailsAccessor.DEFAULT.get()).getUserCode());

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

    @Override
    public Map<Long, Long> getProductIdStockIdMap(String siteId, Long facilityId, String productClassification, String productStockStatusType, Set<Long> productIds) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append(" SELECT product_id                                          ");
        sql.append("      , product_stock_status_id                             ");
        sql.append("   FROM product_stock_status pss                            ");
        sql.append("  WHERE site_id = :siteId                                   ");
        sql.append("    AND facility_id = :facilityId                           ");
        sql.append("    AND product_id IN (:productIds)                         ");
        sql.append("    AND product_stock_status_type = :productStockStatusType ");

        param.put("siteId", siteId);
        param.put("facilityId", facilityId);
        param.put("productIds", productIds);
        param.put("productStockStatusType", productStockStatusType);

        List<Map<String, Object>> list = super.queryForList(sql.toString(), param, null);
        Map<Long, Long> result = new HashMap<>();

        list.forEach(member -> {
            result.put(Long.valueOf(member.get("product_id").toString()), Long.valueOf(member.get("product_stock_status_id").toString()));
        });

        return result;
    }

    @Override
    public Integer countStockStatusQuantitySubLessZero(Set<Long> productStockStatusIds){

        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        sql.append("     SELECT count(1)                                            ");
        sql.append("       FROM product_stock_status                                ");
        sql.append("      WHERE product_stock_status_id in (:productStockStatusIds) ");
        sql.append("        AND quantity < 0                                       ");

        params.put("productStockStatusIds", productStockStatusIds);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public void updateStockStatusQuantityAddForStockList(List<ProductStockStatusVO> stockStatusVOList, Set<Long> productStockStatusIds) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("  UPDATE product_stock_status                            ");
        sql.append("     SET quantity = quantity +                           ");

        sql.append("         CASE product_id        ");

        for (ProductStockStatusVO productStockStatusVO : stockStatusVOList){
            sql.append(" WHEN ").append(productStockStatusVO.getProductId()).append(" THEN ").append(productStockStatusVO.getQuantity());
        }

        sql.append("         END                            ");

        sql.append("       , update_count = update_count + 1                 ");
        sql.append("       , update_program = :updateProgram                 ");
        sql.append("       , last_updated = :currentTime                     ");
        sql.append("       , last_updated_by = :userCd                     ");
        sql.append("   WHERE product_stock_status_id in (:productStockStatusIds) ");

        param.put("updateProgram", stockStatusVOList.iterator().next().getUpdateProgram());
        param.put("productStockStatusIds", productStockStatusIds);
        param.put("currentTime", DateUtils.getCurrentDate());
        param.put("userCd", Objects.isNull(UserDetailsAccessor.DEFAULT.get()) ? stockStatusVOList.iterator().next().getUpdateProgram() : ((PJUserDetails)UserDetailsAccessor.DEFAULT.get()).getUserCode());

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

    @Override
    public List<SPQ030501PrintDetailBO> getPartsOnWorkingCheckPrintList(SPQ030501Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mp.product_cd                                                                                AS partsNo              ");
        sql.append("         , mp.sales_description                                                                         AS partsNm              ");
        sql.append("         , pss.product_id                                                                               AS partsId              ");
        sql.append("         , SUM( CASE WHEN pss.product_stock_status_type = :S018ONRECEIVINGQTY   THEN pss.quantity ELSE 0 END ) AS onReceivingQty       ");
        sql.append("         , SUM( CASE WHEN pss.product_stock_status_type = :S018ONPICKINGQTY     THEN pss.quantity ELSE 0 END ) AS onPickingQty         ");
        sql.append("         , SUM( CASE WHEN pss.product_stock_status_type = :S018ONSERVICEQTY     THEN pss.quantity ELSE 0 end) AS onServiceQty          ");
        sql.append("         , SUM( CASE WHEN pss.product_stock_status_type = :S018ONFROZENQTY      THEN pss.quantity ELSE 0 END ) AS onFrozenQty          ");
        sql.append("         , SUM( CASE WHEN pss.product_stock_status_type = :S018ONTRANSFERINQTY  THEN pss.quantity ELSE 0 END ) AS onTransferInQty      ");
        sql.append("      FROM product_stock_status pss                                                                                             ");
        sql.append(" LEFT JOIN mst_product mp                                                                                                       ");
        sql.append("        ON mp.product_id   = pss.product_id                                                                                     ");
        sql.append("     WHERE pss.site_id     = :siteId                                                                                            ");
        sql.append("       AND pss.facility_id = :facilityId                                                                                        ");

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND pss.product_id = :productId ");
            params.put("productId", form.getPartsId());
        }

        params.put("siteId", siteId);
        params.put("facilityId", form.getPointId());

        params.put("S018ONRECEIVINGQTY", SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        params.put("S018ONPICKINGQTY", SpStockStatus.ONPICKING_QTY.getCodeDbid());
        params.put("S018ONSERVICEQTY", SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        params.put("S018ONFROZENQTY", SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("S018ONTRANSFERINQTY", SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        sql.append(" GROUP BY mp.product_cd,mp.sales_description,pss.product_id ");
        sql.append(" HAVING (SUM(case when pss.product_stock_status_type = :S018ONRECEIVINGQTY then pss.quantity else 0 end )>0 ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONPICKINGQTY then pss.quantity else 0 end )>0    ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONFROZENQTY then pss.quantity else 0 end )>0     ");
	    sql.append("     OR SUM(case when pss.product_stock_status_type = :S018ONTRANSFERINQTY then pss.quantity else 0 end )>0)");
        sql.append(" ORDER BY mp.product_cd ");
        return super.queryForList(sql.toString(), params, SPQ030501PrintDetailBO.class);
    }


    /**
     * 功能描述:dim0206 copy product_stock_status_backup to insert
     *
     * @author mid2178
     */
    @Override
    public void insertFromBK(String siteId, Long facilityId, Set<String> statusTypes){

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> param = new HashMap<>();

        sql.append("  INSERT INTO product_stock_status                    ");
        sql.append("       ( product_stock_status_id                      ");
        sql.append("       , site_id                                      ");
        sql.append("       , facility_id                                  ");
        sql.append("       , product_id                                   ");
        sql.append("       , product_stock_status_type                    ");
        sql.append("       , quantity                                     ");
        sql.append("       , product_classification                       ");
        sql.append("       , update_program                               ");
        sql.append("       , last_updated_by                              ");
        sql.append("       , last_updated                                 ");
        sql.append("       , created_by                                   ");
        sql.append("       , date_created                                 ");
        sql.append("       , update_count)                                ");
        sql.append("  SELECT product_stock_status_id                      ");
        sql.append("       , site_id                                      ");
        sql.append("       , facility_id                                  ");
        sql.append("       , product_id                                   ");
        sql.append("       , product_stock_status_type                    ");
        sql.append("       , qty                                          ");
        sql.append("       , product_classification                       ");
        sql.append("       , update_program                               ");
        sql.append("       , last_updated_by                              ");
        sql.append("       , last_updated                                 ");
        sql.append("       , created_by                                   ");
        sql.append("       , date_created                                 ");
        sql.append("       , update_count                                 ");
        sql.append("    FROM product_stock_status_backup                  ");
        sql.append("   WHERE site_id =:siteId                             ");
        sql.append("     AND facility_id =:facilityId                     ");
        sql.append("     AND product_classification =:productClsType      ");
        sql.append("     AND qty <> 0                                     ");
        sql.append("     AND product_stock_status_type in (:statusTypes)  ");

        param.put("siteId", siteId);
        param.put("facilityId", facilityId);
        param.put("statusTypes", statusTypes);
        param.put("productClsType", ProductClsType.PART.getCodeDbid());

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

    @Override
    public List<SDQ011301BO> findStockInformationInquiry(SDQ011301Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_cd             AS pointCd              ");
        sql.append("          , mf.facility_nm             AS pointNm              ");
        sql.append("          , mp.product_cd              AS modelCd              ");
        sql.append("          , mp.sales_description       AS modelNm              ");
        sql.append("          , mp.color_nm                AS colorNm              ");
        sql.append("          , SUBSTRING(mp.registration_date, 1, 4) AS modelYear ");
        sql.append("          , mp.product_id              AS modelId              ");
        sql.append("          , mf.facility_id             AS pointId              ");
        sql.append("          , SUM( CASE WHEN pss.product_stock_status_type = :ONTRANSITQTY     THEN pss.quantity ELSE 0 END ) AS inTransitQty       ");
        sql.append("          , SUM( CASE WHEN pss.product_stock_status_type = :ONHANDQTY        THEN pss.quantity ELSE 0 END ) AS availableQty       ");
        sql.append("          , SUM( CASE WHEN pss.product_stock_status_type = :ALLOCATEDQTY     THEN pss.quantity ELSE 0 END ) AS allocatedQty       ");
        sql.append("          , SUM( CASE WHEN pss.product_stock_status_type = :ONTRANSFERINQTY  THEN pss.quantity ELSE 0 END ) AS onTransferInQty    ");
        sql.append("       FROM product_stock_status pss                           ");
        sql.append(" INNER JOIN mst_product mp                                     ");
        sql.append("         ON pss.product_id = mp.product_id                     ");
        sql.append(" INNER JOIN mst_facility mf                                    ");
        sql.append("         ON pss.facility_id = mf.facility_id                   ");
        sql.append("      WHERE pss.site_id     = :siteId                          ");
        sql.append("        AND pss.product_classification = :GOODS                ");
        sql.append("        AND mf.facility_id = :pointId                          ");

         if (!ObjectUtils.isEmpty(form.getModelId())) {
            sql.append("      AND mp.product_id = :modelId                         ");
            params.put("modelId",form.getModelId());
         }

        params.put("siteId", form.getSiteId());
        params.put("pointId", form.getPointId());

        params.put("ONTRANSITQTY", SdStockStatus.ONTRANSIT_QTY.getCodeDbid());
        params.put("ONHANDQTY", SdStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("ALLOCATEDQTY", SdStockStatus.ALLOCATED_QTY.getCodeDbid());
        params.put("ONTRANSFERINQTY", SdStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());
        sql.append(" GROUP BY pss.site_id, mf.facility_cd, mf.facility_nm, mp.product_cd, mp.sales_description, mp.color_nm, mp.product_id, mf.facility_id ");
        return super.queryForList(sql.toString(), params, SDQ011301BO.class);
    }
}
