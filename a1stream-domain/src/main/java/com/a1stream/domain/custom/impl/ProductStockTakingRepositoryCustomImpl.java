package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.bo.parts.SPQ030601BO;
import com.a1stream.domain.bo.parts.SPQ030701BO;
import com.a1stream.domain.custom.ProductStockTakingRepositoryCustom;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.a1stream.domain.form.parts.SPQ030601Form;
import com.a1stream.domain.form.parts.SPQ030701Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class ProductStockTakingRepositoryCustomImpl  extends JpaNativeQuerySupportRepository implements ProductStockTakingRepositoryCustom {

    @Override
    public Page<SPQ030601BO> getPartsStocktakingListPageable(SPQ030601Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append(" SELECT pst.seq_no                AS seqNo       ");
        selSql.append("      , w.description             AS wz          ");
        selSql.append("      , pst.workzone_id           AS wzId        ");
        selSql.append("      , l.location_cd             AS location    ");
        selSql.append("      , pst.location_id           AS locationId  ");
        selSql.append("      , mp.product_cd             AS partsNo     ");
        selSql.append("      , mp.sales_description      AS partsNm     ");
        selSql.append("      , pst.product_id            AS partsId     ");
        selSql.append("      , pst.current_average_cost  AS cost        ");
        sql.append("      FROM product_stock_taking pst                 ");
        sql.append(" LEFT JOIN workzone w                               ");
        sql.append("        ON w.site_id     = pst.site_id              ");
        sql.append("       AND w.facility_id = pst.facility_id          ");
        sql.append("       AND w.workzone_id = pst.workzone_id          ");
        sql.append(" LEFT JOIN location l                               ");
        sql.append("       ON l.site_id     = pst.site_id               ");
        sql.append("       AND l.facility_id = pst.facility_id          ");
        sql.append("       AND l.location_id = pst.location_id          ");
        sql.append(" LEFT JOIN mst_product mp                           ");
        sql.append("       ON mp.product_id  = pst.product_id          ");
        sql.append("     WHERE pst.site_id = :siteId                    ");
        sql.append("       AND pst.facility_id = :pointId               ");

        params.put("siteId", siteId);
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getWzId())) {
            sql.append("   AND pst.workzone_id = :wzId ");
            params.put("wzId", form.getWzId());
        }
        sql.append("  ORDER BY pst.seq_no ");
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ030601BO.class, pageable), pageable, count);
    }

    @Override
    public Page<SPQ030701BO> getPartsStocktakingProgressList(SPQ030701Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("    SELECT pst.seq_no                AS seqNo           ");
        selSql.append("         , w.description             AS wz              ");
        selSql.append("         , pst.workzone_id           AS wzId            ");
        selSql.append("         , l.location_cd             AS location        ");
        selSql.append("         , pst.location_id           AS locationId      ");
        selSql.append("         , mp.product_cd             AS partsNo         ");
        selSql.append("         , mp.sales_description      AS partsNm         ");
        selSql.append("         , pst.product_id            AS partsId         ");
        selSql.append("         , pst.expected_qty          AS systemQty       ");
        selSql.append("         , pst.actual_qty            AS actualQty       ");
        selSql.append("         , pst.input_flag            AS inputSign       ");
        selSql.append("         , pst.new_found_flag        AS newFoundFlag    ");
        sql.append("         FROM product_stock_taking pst                     ");
        sql.append("    LEFT JOIN workzone w                                   ");
        sql.append("           ON w.site_id     = pst.site_id                  ");
        sql.append("          AND w.facility_id = pst.facility_id              ");
        sql.append("          AND w.workzone_id = pst.workzone_id              ");
        sql.append("    LEFT JOIN location l                                   ");
        sql.append("           ON l.site_id     = pst.site_id                  ");
        sql.append("          AND l.facility_id = pst.facility_id              ");
        sql.append("          AND l.location_id = pst.location_id              ");
        sql.append("    LEFT JOIN mst_product mp                               ");
        sql.append("           ON mp.product_id  = pst.product_id                 ");
        sql.append("        WHERE pst.site_id = :siteId                        ");
        sql.append("          AND pst.facility_id = :pointId                   ");

        params.put("siteId", siteId);
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND pst.product_id  = :partsId ");
            params.put("partsId", form.getPartsId());
        }
        if (!Nulls.isNull(form.getWzId())) {
            sql.append(" AND pst.workzone_id = :wzId ");
            params.put("wzId", form.getWzId());
        }
        if (StringUtils.isNotBlank(form.getInputSign())) {
            sql.append(" AND pst.input_flag  = :inputFlag ");
            params.put("inputFlag", form.getInputSign());
        }
        if (CommonConstants.CHAR_Y.equals(form.getShowDifferentOnly())) {
            sql.append(" AND pst.expected_qty  != pst.actual_qty ");
        }
        sql.append("  ORDER BY pst.seq_no                               ");
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ030701BO.class, pageable), pageable, count);
    }

    @Override
    public Map<String,SPM031001BO> getProductStockTakingByType(String siteId, Long facilityId, String productClassification) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Map<String, SPM031001BO> returnMap = new HashMap<>();

        sql.append("  select :systemTotal                          as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(expected_qty), 0)         as qty     ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortfirst                             as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and new_found_flag = :newFoundFlag                    ");
        sql.append("  union all                                               ");
        sql.append(" select :actualTotal                           as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(actual_qty), 0)           as qty     ");
        sql.append("      , coalesce(sum(actual_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortSecond                            as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyEqual                             as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(expected_qty), 0)         as qty     ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost), 0) as amount  ");
        sql.append("      , :sortThird                             as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty = expected_qty                         ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyExceed                as type                ");
        sql.append("      , count(seq_no)              as lines               ");
        sql.append("      , count(distinct product_id) as items               ");
        sql.append("      , coalesce(sum(actual_qty-expected_qty), 0)  as qty ");
        sql.append("      , coalesce(sum(actual_qty*current_average_cost - expected_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortFourth                as seq                 ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty > expected_qty                         ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyLack                  as type                ");
        sql.append("      , count(seq_no)              as lines               ");
        sql.append("      , count(distinct product_id) as items               ");
        sql.append("      , coalesce(sum(expected_qty-actual_qty), 0)  as qty ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost - actual_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortFifth                 as seq                 ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty < expected_qty                         ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", productClassification);

        params.put("systemTotal", PJConstants.StockTakingType.SYSTEM_TOTAL);
        params.put("actualTotal", PJConstants.StockTakingType.ACTUAL_TOTAL);
        params.put("qtyEqual", PJConstants.StockTakingType.QTYEQUAL);
        params.put("qtyExceed", PJConstants.StockTakingType.QTYEXCEED);
        params.put("qtyLack", PJConstants.StockTakingType.QTYLACK);

        params.put("sortfirst", CommonConstants.CHAR_ONE);
        params.put("sortSecond", CommonConstants.CHAR_TWO);
        params.put("sortThird", CommonConstants.CHAR_THREE);
        params.put("sortFourth", CommonConstants.CHAR_FOUR);
        params.put("sortFifth", CommonConstants.CHAR_FIVE);
        params.put("newFoundFlag", CommonConstants.CHAR_N);

        List<SPM031001BO> spm031001BOs = super.queryForList(sql.toString(), params, SPM031001BO.class);
        for (SPM031001BO BO : spm031001BOs) {
            returnMap.put(BO.getType() , BO);
        }

        return returnMap;
    }

    @Override
    public List<SPM031001BO> getStockTakingSummary(String siteId, Long facilityId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  select    distinct pst.product_id as productId                              ");
        sql.append("          , coalesce(sum(pst.actual_qty), 0) as actualQty                     ");
        sql.append("          , coalesce(sum(pst.expected_qty), 0) as expectedQty                 ");
        sql.append("          , case when l.location_type in (:normal, :tentative)                ");
        sql.append("                 then :onHand                                                 ");
        sql.append("                 else (case when l.location_type in (:frozen)                 ");
        sql.append("                            then :onFrozen                                    ");
        sql.append("                            else '' end) end as stockStatusType               ");
        sql.append("          , :qtyExceed  as type                                               ");
        sql.append("        from product_stock_taking pst                                         ");
        sql.append("  inner join location l                                                       ");
        sql.append("          on l.location_id  = pst.location_id                                 ");
        sql.append("         and l.location_type in (:normal, :tentative,:frozen)                 ");
        sql.append("       where pst.site_id =:siteId                                             ");
        sql.append("         and pst.facility_id =:facilityId                                     ");
        sql.append("         and pst.product_classification =:productClassification               ");
        sql.append("         and pst.product_id is not null                                       ");
        sql.append("         and pst.actual_qty > pst.expected_qty                                ");
        sql.append("    group by pst.product_id, stockStatusType                                  ");
        sql.append("  union all                                                                   ");
        sql.append("  select    distinct pst.product_id as productId                              ");
        sql.append("          , coalesce(sum(pst.actual_qty), 0) as actualQty                     ");
        sql.append("          , coalesce(sum(pst.expected_qty), 0) as expectedQty                 ");
        sql.append("          , case when l.location_type in (:normal, :tentative)                ");
        sql.append("                 then :onHand                                                 ");
        sql.append("                 else (case when l.location_type in (:frozen)                 ");
        sql.append("                            then :onFrozen                                    ");
        sql.append("                            else '' end) end as stockStatusType               ");
        sql.append("          , :qtyLack as type                                                  ");
        sql.append("        from product_stock_taking pst                                         ");
        sql.append("  inner join location l                                                       ");
        sql.append("          on l.location_id  = pst.location_id                                 ");
        sql.append("         and l.location_type in (:normal, :tentative,:frozen)                 ");
        sql.append("       where pst.site_id =:siteId                                             ");
        sql.append("         and pst.facility_id =:facilityId                                     ");
        sql.append("         and pst.product_classification =:productClassification               ");
        sql.append("         and pst.product_id is not null                                       ");
        sql.append("         and pst.actual_qty < pst.expected_qty                                ");
        sql.append("    group by pst.product_id, stockStatusType                                  ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("qtyExceed", PJConstants.StockTakingType.QTYEXCEED);
        params.put("qtyLack", PJConstants.StockTakingType.QTYLACK);

        params.put("onHand", SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("onFrozen", SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("normal", LocationType.NORMAL.getCodeDbid());
        params.put("tentative", LocationType.TENTATIVE.getCodeDbid());
        params.put("frozen", LocationType.FROZEN.getCodeDbid());

        return super.queryForList(sql.toString(), params, SPM031001BO.class);
    }

    @Override
    public List<SPM031001BO> getProductStockTakingList(String siteId, Long facilityId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     select pst.product_id           as productId               ");
        sql.append("          , mp.product_cd            as productCd               ");
        sql.append("          , mp.sales_description     as productNm               ");
        sql.append("          , l.location_id            as locationId              ");
        sql.append("          , l.location_cd            as locationCd              ");
        sql.append("          , coalesce(pst.actual_qty, 0) as actualQty            ");
        sql.append("          , coalesce(pst.expected_qty, 0) as expectedQty        ");
        sql.append("          , pi2.product_inventory_id as productInventoryId      ");
        sql.append("          , pst.current_average_cost as inCost                  ");
        sql.append("          , coalesce(pc.cost, 0) as currentAverageCost          ");
        sql.append("       from product_stock_taking pst                            ");
        sql.append(" inner join location l                                          ");
        sql.append("         on l.location_id = pst.location_id                     ");
        sql.append(" inner join mst_product mp                                      ");
        sql.append("         on mp.product_id = pst.product_id                      ");
        sql.append("  left join product_cost pc                                     ");
        sql.append("         on pc.product_id = pst.product_id                      ");
        sql.append("        and pc.cost_type = :costType                            ");
        sql.append("        and pc.site_id = :siteId                                ");
        sql.append("  left join product_inventory pi2                               ");
        sql.append("         on pi2.product_id = pst.product_id                     ");
        sql.append("        and pi2.location_id = pst.location_id                   ");
        sql.append("        and pi2.site_id =:siteId                                ");
        sql.append("        and pi2.facility_id =:facilityId                        ");
        sql.append("        and pi2.product_classification =:productClassification  ");
        sql.append("      where pst.site_id =:siteId                                ");
        sql.append("        and pst.facility_id =:facilityId                        ");
        sql.append("        and pst.product_classification =:productClassification  ");
        sql.append("        and pst.product_id is not null                          ");
        sql.append("        and pst.actual_qty <> pst.expected_qty                  ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());
        params.put("costType", PJConstants.CostType.AVERAGE_COST);

        return super.queryForList(sql.toString(), params, SPM031001BO.class);
    }

    /**
     * anthor : Tang Tiantian
     */
    @Override
    public List<SPM030901BO> listProductStockTaking(SPM030901Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT pst.product_stock_taking_id AS productStockTakingId    ");
        sql.append("            ,pst.facility_id             AS pointId               ");
        sql.append("            ,pst.range_type              AS rangeType               ");
        sql.append("            ,pst.seq_no                  AS seqNo                   ");
        sql.append("            ,pst.workzone_id             AS workzoneId              ");
        sql.append("            ,pst.location_id             AS locationId              ");
        sql.append("            ,pst.product_id              AS productId               ");
        sql.append("            ,pst.actual_qty              AS actualQty               ");
        sql.append("            ,pst.current_average_cost    AS currentAverageCost      ");
        sql.append("            ,pst.input_flag              AS inputFlag               ");
        sql.append("            ,pst.new_found_flag          AS newFoundFlag            ");
        sql.append("            ,pst.started_date            AS startedDate             ");
        sql.append("            ,pst.started_time            AS startedTime             ");
        sql.append("            ,mp.product_cd               AS productCd               ");
        sql.append("            ,mp.sales_description        AS productName             ");
        sql.append("        FROM product_stock_taking pst                               ");
        sql.append("   LEFT JOIN mst_product mp                                         ");
        sql.append("          ON mp.product_id = pst.product_id                         ");
        sql.append("       WHERE pst.site_id  = :siteId                                 ");
        sql.append("         AND pst.facility_id = :facilityId                          ");

        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getPointId());

        if(form.getSeqNoFrom() != null) {
            sql.append("     AND pst.seq_no >= :seqNoFrom                          ");
            params.put("seqNoFrom", form.getSeqNoFrom());
        }

        if(form.getSeqNoTo() != null) {
            sql.append("     AND pst.seq_no <= :seqNoTo                            ");
            params.put("seqNoTo", form.getSeqNoTo());
        }

        if(form.getWzId() != null) {
            sql.append("     AND pst.workzone_id = :wzId                          ");
            params.put("wzId", form.getWzId());
        }

        if(form.getPartsId() != null) {
            sql.append("     AND pst.product_id = :partsId                        ");
            params.put("partsId", form.getPartsId());
        }

        if(form.getLocationId() != null) {
            sql.append("     AND pst.location_id = :locationId                        ");
            params.put("locationId", form.getLocationId());
        }

        // 如果勾选了Non Input Flag ,则只显示还未登记真实库存的parts
        if(StringUtils.equals(form.getNoneInputOnly(), CommonConstants.CHAR_Y)) {
            sql.append("     AND pst.input_flag = :nonInputFlag                        ");
            params.put("nonInputFlag", CommonConstants.CHAR_N);
        }

        if(StringUtils.equals(form.getShowDiff(), CommonConstants.CHAR_Y)) {
            sql.append("     AND pst.expected_qty <> pst.actual_qty                        ");
        }

        sql.append("       ORDER BY pst.seq_no ;                                        ");

        return super.queryForList(sql.toString(), params, SPM030901BO.class);
    }

    @Override
    public Map<String,SPM031001BO> getPrintPartsStocktakingResultList(Long facilityId, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Map<String, SPM031001BO> returnMap = new HashMap<>();

        sql.append("  select :systemTotal                          as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(expected_qty), 0)         as qty     ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortfirst                             as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and new_found_flag = :newFoundFlag                    ");
        sql.append("  union all                                               ");
        sql.append(" select :actualTotal                           as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(actual_qty), 0)           as qty     ");
        sql.append("      , coalesce(sum(actual_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortSecond                            as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyEqual                             as type    ");
        sql.append("      , count(seq_no)                          as lines   ");
        sql.append("      , count(distinct product_id)             as items   ");
        sql.append("      , coalesce(sum(expected_qty), 0)         as qty     ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortThird                             as seq     ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty = expected_qty                         ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyExceed                as type                ");
        sql.append("      , count(seq_no)              as lines               ");
        sql.append("      , count(distinct product_id) as items               ");
        sql.append("      , coalesce(sum(actual_qty-expected_qty), 0)  as qty ");
        sql.append("      , coalesce(sum(actual_qty*current_average_cost - expected_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortFourth                as seq                 ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty > expected_qty                         ");
        sql.append("  union all                                               ");
        sql.append("  select :qtyLack                  as type                ");
        sql.append("      , count(seq_no)              as lines               ");
        sql.append("      , count(distinct product_id) as items               ");
        sql.append("      , coalesce(sum(expected_qty-actual_qty), 0)  as qty ");
        sql.append("      , coalesce(sum(expected_qty*current_average_cost - actual_qty*current_average_cost), 0) as amount ");
        sql.append("      , :sortFifth                 as seq                 ");
        sql.append("   from product_stock_taking                              ");
        sql.append("  where site_id =:siteId                                  ");
        sql.append("    and facility_id =:facilityId                          ");
        sql.append("    and product_classification =:productClassification    ");
        sql.append("    and product_id is not null                            ");
        sql.append("    and actual_qty < expected_qty                         ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("productClassification", ProductClsType.PART.getCodeDbid());

        params.put("systemTotal", PJConstants.StockTakingTypeVietnamese.SYSTEM_TOTAL);
        params.put("actualTotal", PJConstants.StockTakingTypeVietnamese.ACTUAL_TOTAL);
        params.put("qtyEqual", PJConstants.StockTakingTypeVietnamese.QTYEQUAL);
        params.put("qtyExceed", PJConstants.StockTakingTypeVietnamese.QTYEXCEED);
        params.put("qtyLack", PJConstants.StockTakingTypeVietnamese.QTYLACK);

        params.put("sortfirst", CommonConstants.CHAR_ONE);
        params.put("sortSecond", CommonConstants.CHAR_TWO);
        params.put("sortThird", CommonConstants.CHAR_THREE);
        params.put("sortFourth", CommonConstants.CHAR_FOUR);
        params.put("sortFifth", CommonConstants.CHAR_FIVE);
        params.put("newFoundFlag", CommonConstants.CHAR_N);

        List<SPM031001BO> spm031001BOs = super.queryForList(sql.toString(), params, SPM031001BO.class);
        for (SPM031001BO BO : spm031001BOs) {
            returnMap.put(BO.getType() , BO);
        }

        return returnMap;
    }

    @Override
    public List<SPQ030701BO> getPrintPartsStocktakingProgressList(SPQ030701Form form, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mf.facility_cd || ' / ' || mf.facility_nm point ");
        sql.append("         , pst.seq_no                AS seqNo           ");
        sql.append("         , w.description             AS wz              ");
        sql.append("         , pst.workzone_id           AS wzId            ");
        sql.append("         , l.location_cd             AS location        ");
        sql.append("         , pst.location_id           AS locationId      ");
        sql.append("         , mp.product_cd             AS partsNo         ");
        sql.append("         , mp.sales_description      AS partsNm         ");
        sql.append("         , pst.product_id            AS partsId         ");
        sql.append("         , pst.expected_qty          AS systemQty       ");
        sql.append("         , pst.actual_qty            AS actualQty       ");
        sql.append("         , pst.input_flag            AS inputSign       ");
        sql.append("         , pst.new_found_flag        AS newFoundFlag    ");
        sql.append("         FROM product_stock_taking pst                  ");
        sql.append("    LEFT JOIN mst_facility mf                           ");
        sql.append("           ON mf.facility_id = pst.facility_id          ");
        sql.append("    LEFT JOIN workzone w                                ");
        sql.append("           ON w.site_id     = pst.site_id               ");
        sql.append("          AND w.facility_id = pst.facility_id           ");
        sql.append("          AND w.workzone_id = pst.workzone_id           ");
        sql.append("    LEFT JOIN location l                                ");
        sql.append("           ON l.site_id     = pst.site_id               ");
        sql.append("          AND l.facility_id = pst.facility_id           ");
        sql.append("          AND l.location_id = pst.location_id           ");
        sql.append("    LEFT JOIN mst_product mp                            ");
        sql.append("           ON mp.site_id     = pst.site_id              ");
        sql.append("          AND mp.product_id  = pst.product_id           ");
        sql.append("        WHERE pst.site_id = :siteId                     ");
        sql.append("          AND pst.facility_id = :pointId                ");

        params.put("siteId", siteId);
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getPartsId())) {
            sql.append(" AND pst.product_id  = :partsId ");
            params.put("partsId", form.getPartsId());
        }
        if (!Nulls.isNull(form.getWzId())) {
            sql.append(" AND pst.workzone_id = :wzId ");
            params.put("wzId", form.getWzId());
        }
        if (StringUtils.isNotBlank(form.getInputSign())) {
            sql.append(" AND pst.input_flag  = :inputFlag ");
            params.put("inputFlag", form.getInputSign());
        }
        if (CommonConstants.CHAR_Y.equals(form.getShowDifferentOnly())) {
            sql.append(" AND pst.expected_qty  != pst.actual_qty ");
        }
        sql.append("  ORDER BY pst.seq_no                               ");

        return super.queryForList(sql.toString(), params, SPQ030701BO.class);
    }

    /**
     * 功能描述:打印报表PartsStocktakingResultStatistics
     * @author : Liu Chaoran
     */
    @Override
    public List<SPM031001BO> getPartsStocktakingResultStatisticsList(String siteId, Long facilityId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mpc.category_cd                                         AS middleGroupCd   ");
        sql.append("          , mpc.category_nm                                         AS middlegroupNm   ");
        sql.append("          , mpc.parent_category_cd                                  AS largeGroupCd    ");
        sql.append("          , mpc.parent_category_nm                                  AS largeGroupNm    ");
        sql.append("          , coalesce(pst.actual_qty, 0) * pst.current_average_cost  AS amount          ");
        sql.append("       FROM product_stock_taking pst                                                   ");
        sql.append(" INNER JOIN mst_product mp                                                             ");
        sql.append("         ON mp.product_id = pst.product_id                                             ");
        sql.append(" INNER JOIN mst_product_category mpc                                                   ");
        sql.append("         ON mpc.product_category_id = mp.product_category_id                           ");
        sql.append("      WHERE pst.site_id =:siteId                                                       ");
        sql.append("        AND pst.facility_id =:facilityId                                               ");
        sql.append("   ORDER BY mpc.category_cd ,mpc.parent_category_cd                                    ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);

        return super.queryForList(sql.toString(), params, SPM031001BO.class);
    }

    @Override
    public List<SPM031001BO> getPrintPartsStocktakingGapList(String siteId, Long facilityId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_cd                                 AS productCd              ");
        sql.append("          , mp.sales_description                          AS productNm              ");
        sql.append("          , l.location_cd                                 AS locationCd             ");
        sql.append("          , coalesce(pst.current_average_cost, 0.0000)    AS cost                   ");
        sql.append("          , coalesce(pst.expected_qty, 0)                 AS expectedQty            ");
        sql.append("          , coalesce(pst.actual_qty, 0)                   AS actualQty              ");
        sql.append("       FROM product_stock_taking pst                                                ");
        sql.append(" INNER JOIN mst_facility mf                                                         ");
        sql.append("         ON mf.facility_id = pst.facility_id                                        ");
        sql.append("  LEFT JOIN mst_product mp                                                          ");
        sql.append("         ON mp.product_id = pst.product_id                                          ");
        sql.append("  LEFT JOIN location l                                                              ");
        sql.append("         ON l.location_id = pst.location_id                                         ");
        sql.append("        AND l.facility_id = pst.facility_id                                         ");
        sql.append("      WHERE pst.site_id =:siteId                                                    ");
        sql.append("        AND pst.facility_id =:facilityId                                            ");
        sql.append("        AND pst.actual_qty > 0                                                      ");
        sql.append("        AND pst.expected_qty != pst.actual_qty                                      ");
        sql.append("   ORDER BY mp.product_cd ,l.location_id                                            ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);

        return super.queryForList(sql.toString(), params, SPM031001BO.class);
    }

    @Override
    public List<SPM031001BO> getPrintPartsStocktakingLedgerList(String siteId, Long facilityId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_cd                                           AS productCd    ");
        sql.append("          , mp.sales_description                                    AS productNm    ");
        sql.append("          , coalesce(pst.current_average_cost, 0.0000)              AS averageCost  ");
        sql.append("          , coalesce(pst.actual_qty, 0)                             AS actualQty    ");
        sql.append("          , mpc.category_nm                                         AS middleGroupNm");
        sql.append("          , coalesce(pst.actual_qty, 0) * pst.current_average_cost  AS amount       ");
        sql.append("       FROM product_stock_taking pst                                                ");
        sql.append(" INNER JOIN mst_product mp                                                          ");
        sql.append("         ON mp.product_id = pst.product_id                                          ");
        sql.append(" INNER JOIN mst_product_category mpc                                                ");
        sql.append("         ON mpc.product_category_id = mp.product_category_id                        ");
        sql.append("      WHERE pst.site_id =:siteId                                                    ");
        sql.append("        AND pst.facility_id =:facilityId                                            ");
        sql.append("        AND pst.actual_qty > 0                                                      ");
        sql.append("   ORDER BY mp.product_cd ,mpc.category_nm                                          ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);

        return super.queryForList(sql.toString(), params, SPM031001BO.class);
    }

    @Override
    public List<SPQ030601BO> getPrintPartsStocktakingResultList(SPQ030601Form form, String siteId){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT pst.seq_no                     AS seqNo         ");
        sql.append("         , mf.facility_cd || ' / ' || mf.facility_nm AS point           ");
        sql.append("         , w.description                  AS wz            ");
        sql.append("         , l.location_cd                  AS location      ");
        sql.append("         , mp.product_cd                  AS partsNo       ");
        sql.append("         , mp.sales_description           AS partsNm       ");
        sql.append("         , COALESCE(pst.expected_qty,0)   AS currentStock  ");
        sql.append("      FROM product_stock_taking pst                        ");
        sql.append(" LEFT JOIN workzone w                                      ");
        sql.append("        ON w.site_id     = pst.site_id                     ");
        sql.append("       AND w.facility_id = pst.facility_id                 ");
        sql.append("       AND w.workzone_id = pst.workzone_id                 ");
        sql.append(" LEFT JOIN mst_facility mf                                 ");
        sql.append("        ON pst.facility_id = mf.facility_id                ");
        sql.append(" LEFT JOIN location l                                      ");
        sql.append("        ON l.site_id     = pst.site_id                     ");
        sql.append("       AND l.facility_id = pst.facility_id                 ");
        sql.append("       AND l.location_id = pst.location_id                 ");
        sql.append(" LEFT JOIN mst_product mp                                  ");
        sql.append("        ON mp.site_id     = pst.site_id                    ");
        sql.append("       AND mp.product_id  = pst.product_id                 ");
        sql.append("     WHERE pst.site_id = :siteId                           ");
        sql.append("       AND pst.facility_id = :pointId                      ");

        params.put("siteId", siteId);
        params.put("pointId", form.getPointId());

        if (!Nulls.isNull(form.getWzId())) {
            sql.append("   AND pst.workzone_id = :wzId ");
            params.put("wzId", form.getWzId());
        }
        sql.append("  ORDER BY pst.seq_no, w.description, pst.seq_no, w.workzone_cd, l.location_cd, mp.product_cd ");

        return super.queryForList(sql.toString(), params, SPQ030601BO.class);
    }
}
