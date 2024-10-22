package com.a1stream.domain.custom.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CategoryCd;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductRelationClass;
import com.a1stream.common.constants.PJConstants.RopRoqParameter;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.model.BaseVLBO;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ModelVLBO;
import com.a1stream.common.model.PartsInfoBO;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.batch.PartsDeadStockItemBO;
import com.a1stream.domain.bo.master.CMM040301BO;
import com.a1stream.domain.bo.master.CMM050101BO;
import com.a1stream.domain.bo.master.CMM050102BasicInfoBO;
import com.a1stream.domain.bo.master.CMM050102PurchaseControlBO;
import com.a1stream.domain.bo.master.CMM050102SalesControlBO;
import com.a1stream.domain.bo.master.CMM060101BO;
import com.a1stream.domain.bo.master.CMQ050101BasicInfoBO;
import com.a1stream.domain.bo.master.CMQ050101DemandDetailBO;
import com.a1stream.domain.bo.master.CMQ050101InformationBO;
import com.a1stream.domain.bo.master.CMQ050101PurchaseControlBO;
import com.a1stream.domain.bo.master.CMQ050101StockInfoBO;
import com.a1stream.domain.bo.master.CMQ050801BO;
import com.a1stream.domain.bo.parts.SPM040601BO;
import com.a1stream.domain.bo.service.ProductBO;
import com.a1stream.domain.bo.unit.CMM090101BO;
import com.a1stream.domain.custom.MstProductRepositoryCustom;
import com.a1stream.domain.form.master.CMM040301Form;
import com.a1stream.domain.form.master.CMM050101Form;
import com.a1stream.domain.form.master.CMM050102Form;
import com.a1stream.domain.form.master.CMM060101Form;
import com.a1stream.domain.form.master.CMQ050101Form;
import com.a1stream.domain.form.master.CMQ050801Form;
import com.a1stream.domain.form.unit.CMM090101Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;


public class MstProductRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements MstProductRepositoryCustom {

    @Override
    public ValueListResultBO findModelList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT product_id         AS id      ");
        sql.append("      , product_cd         AS code    ");
        sql.append("      , sales_description  AS name    ");
        sql.append("      , concat(product_cd, ' ', sales_description)   AS desc    ");
        sql.append("   FROM mst_product                   ");
        sql.append("  WHERE site_id = :siteId             ");
        sql.append("    AND product_classification = :pc  ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("pc", ProductClsType.GOODS.getCodeDbid());

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND REPLACE(upper(product_retrieve), ' ', '')  LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("  ORDER BY product_cd                    ");

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

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public ValueListResultBO findSdModelList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mp.product_id         AS modelId     ");
        sql.append("      , mp.product_cd         AS modelCd     ");
        sql.append("      , mp.sales_description  AS modelNm     ");
        sql.append("      , mp.expired_date       AS expiredDate ");
        sql.append("      , mp.color_nm           AS colorNm     ");

        sql.append("      , substring(mp.registration_date, 1, 4) AS modelYear       ");
        sql.append("      , substring(mp.product_cd, 1, 4)        AS registeredModel ");
        sql.append("      , concat(product_cd, ' ', sales_description)   AS desc     ");

        sql.append("   FROM mst_product mp                ");
        sql.append("  WHERE product_classification = :pc  ");
        sql.append("    AND mp.product_level = :one");

        params.put("pc", ProductClsType.GOODS.getCodeDbid());
        params.put("one", CommonConstants.INTEGER_ONE);

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND REPLACE(upper(product_retrieve), ' ', '')  LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        if(StringUtils.isNotBlank(model.getArg0())){
            sql.append(" AND substring(registration_date, 1, 4) = :year ");
            params.put("year",model.getArg0());
        }

        if(StringUtils.isNotBlank(model.getArg1())){
            sql.append(" AND mp.product_cd = :modelCd ");
            params.put("modelCd",model.getArg1());
        }

        sql.append("  ORDER BY product_cd asc         ");

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

        List<ModelVLBO> result = super.queryForList(sql.toString(), params, ModelVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public ValueListResultBO findPartsList(PartsVLForm model, String siteId) {
        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH p_list AS (                                         ");
        sql.append("    SELECT mb.brand_nm AS brand                          ");
        sql.append("         , mp.product_id AS productId                    ");
        sql.append("         , mp.product_cd AS productCd                    ");
        sql.append("         , mp.sales_description AS productNm             ");
        sql.append("         , mp.sal_lot_size AS salLotSize                 ");
        sql.append("         , mp.min_pur_qty AS minPurQty                   ");
        sql.append("         , mp.std_retail_price AS stdRetailPrice         ");
        sql.append("         , mp.battery_flag AS batteryFlag                ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup    ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup   ");
        sql.append("         , mp.product_retrieve    AS productRetrieve     ");
        sql.append("         , mp.brand_id            AS brandId             ");
        sql.append("         , mp.all_parent_id       AS allParentId         ");
        sql.append("         , mp.sales_status_type   AS partStatus          ");
        sql.append("         , mp.product_category_id                        ");
        sql.append("         , mp.pur_lot_size        AS purLotSize          ");
        sql.append("      FROM mst_product mp                                ");
        sql.append("         , mst_brand mb                                  ");
        sql.append("     WHERE mp.site_id = :siteId1                         ");
        sql.append("       AND mp.product_classification = :pc               ");
        sql.append("       AND mb.site_id = mp.site_id                       ");
        sql.append("       AND mb.brand_id = mp.brand_id                     ");

        if (!Objects.isNull(model.getPartIds()) && !model.getPartIds().isEmpty()) {
            sql.append("   AND mp.product_id in (:partsIds)                   ");
            params.put("partsIds", model.getPartIds());
        }

        sql.append(" UNION ALL                                               ");
        sql.append("    SELECT mb.brand_nm AS brand                          ");
        sql.append("         , mp.product_id AS productId                    ");
        sql.append("         , mp.product_cd AS productCd                    ");
        sql.append("         , mp.sales_description AS productNm             ");
        sql.append("         , mp.sal_lot_size AS salLotSize                 ");
        sql.append("         , mp.min_pur_qty AS minPurQty                   ");
        sql.append("         , mp.std_retail_price AS stdRetailPrice         ");
        sql.append("         , mp.battery_flag AS batteryFlag                ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup    ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup   ");
        sql.append("         , mp.product_retrieve AS productRetrieve        ");
        sql.append("         , mp.brand_id            AS brandId             ");
        sql.append("         , mp.all_parent_id       AS allParentId         ");
        sql.append("         , mp.sales_status_type   AS partStatus          ");
        sql.append("         , mp.product_category_id                        ");
        sql.append("         , mp.pur_lot_size        AS purLotSize          ");
        sql.append("    FROM mst_product mp                                  ");
        sql.append("       , mst_brand mb                                    ");
        sql.append("   WHERE mp.site_id = :siteId2                           ");
        sql.append("     AND mp.product_classification = :pc                 ");
        sql.append("     AND mb.brand_id = mp.brand_id                       ");

        if (!Objects.isNull(model.getPartIds()) && !model.getPartIds().isEmpty()) {
            sql.append("   AND mp.product_id in (:partsIds)                   ");
            params.put("partsIds", model.getPartIds());
        }

        sql.append("    )                                                    ");
        sql.append("    SELECT pl.brand             AS brand                 ");
        sql.append("         , pl.productId         AS id                    ");
        sql.append("         , pl.productCd         AS code                  ");
        sql.append("         , pl.productNm         AS name                  ");
        sql.append("         , concat(pl.productCd, ' ', pl.productNm)   AS desc                  ");
        sql.append("         , pl.salLotSize        AS salLotSize            ");
        sql.append("         , pl.stdRetailPrice    AS stdRetailPrice        ");
        sql.append("         , pl.batteryFlag       AS batteryFlag           ");
        sql.append("         , pl.largeGroup        AS largeGroup            ");
        sql.append("         , pl.middlegroup       AS middleGroup           ");
        sql.append("         , pl.productRetrieve   AS productRetrieve       ");
        sql.append("         , pl.brandId           AS brandId               ");
        sql.append("         , pl.allParentId       AS allParentId           ");
        sql.append("         , pl.partStatus        AS partStatus            ");
        sql.append("         , pl.minPurQty         AS minPurQty             ");
        sql.append("         , COALESCE(pc2.cost, pc.cost, 0)     AS price   ");
        sql.append("         , pl.purLotSize        AS purLotSize            ");
        sql.append("      FROM p_list pl                                     ");
        sql.append(" LEFT JOIN product_cost pc                               ");
        sql.append("        ON pc.site_id = :siteId2                         ");
        sql.append("       AND pc.product_id = pl.productId                  ");
        sql.append("       AND pc.cost_type = :costType                      ");
        sql.append(" LEFT JOIN product_cost pc2                              ");
        sql.append("        ON pc2.site_id = :siteId2                        ");
        sql.append("       AND pc2.product_id = pl.productId                 ");
        sql.append("       AND pc2.cost_type = :costType2                    ");
        sql.append("      WHERE 1 = 1                                        ");

        params.put("siteId1", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("siteId2", siteId);
        params.put("pc", ProductClsType.PART.getCodeDbid());
        params.put("costType", CostType.RECEIVE_COST);
        params.put("costType2", CostType.AVERAGE_COST);

        if (StringUtils.equals(model.getBatteryFlag(), CommonConstants.CHAR_Y)) {
            sql.append(" AND pl.batteryFlag = :batteryFlag ");
            params.put("batteryFlag", model.getBatteryFlag());
        }

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND pl.productRetrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        if (StringUtils.isNotBlank(model.getCode())) {
            sql.append(" AND pl.productRetrieve LIKE :productRetrieve ");
            params.put("productRetrieve", CommonConstants.CHAR_PERCENT + model.getCode().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }
        if (StringUtils.isNotBlank(model.getPartsCd())) {
            sql.append(" AND pl.productCd = :partCd ");
            params.put("partCd", model.getPartsCd());
        }

        //ypec查询数据用
        if (!Nulls.isNull(model.getPartsCds())) {
            sql.append(" AND pl.productCd in :partCds ");
            params.put("partCds", model.getPartsCds());
        }

        if (StringUtils.isNotBlank(model.getBrandId())) {
            sql.append(" AND CAST(pl.brandId AS VARCHAR) = :brandId ");
            params.put("brandId", model.getBrandId());
        }

        if (model.getProdCtg() != null && !model.getProdCtg().isEmpty()) {
            if (model.getProdCtg().size() == 1) {
                sql.append(" AND split_part(pl.allParentId, '|', 1) = :largeGroupId ");
                params.put("largeGroupId", model.getProdCtg().get(0));
            } else if(model.getProdCtg().size() == 2) {
                sql.append(" AND split_part(pl.allParentId, '|', 1) = :largeGroupId ");
                sql.append(" AND split_part(pl.allParentId, '|', 2) = :middleGroupId ");
                params.put("largeGroupId", model.getProdCtg().get(0));
                params.put("middleGroupId", model.getProdCtg().get(1));
            }
        }

        if (StringUtils.isNotBlank(model.getLargeMiddleGroup())) {
            sql.append(" AND pl.allParentId like :largeMiddleGroup ");
            params.put("largeMiddleGroup", model.getLargeMiddleGroup().concat(CommonConstants.CHAR_PERCENT));
        }

        sql.append("  ORDER BY pl.productCd                            ");

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

        List<PartsVLBO> result = super.queryForList(sql.toString(), params, PartsVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public ValueListResultBO findYamahaPartsList(PartsVLForm model) {
        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH p_list AS (                                                      ");
        sql.append("    SELECT mb.brand_nm                  AS brand                      ");
        sql.append("         , mp.product_id                AS productId                  ");
        sql.append("         , mp.product_cd                AS productCd                  ");
        sql.append("         , mp.sales_description         AS productNm                  ");
        sql.append("         , mp.sal_lot_size              AS salLotSize                 ");
        sql.append("         , mp.min_pur_qty               AS minPurQty                  ");
        sql.append("         , mp.std_retail_price          AS stdRetailPrice             ");
        sql.append("         , mp.battery_flag              AS batteryFlag                ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup                 ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup                ");
        sql.append("         , mp.product_retrieve          AS productRetrieve            ");
        sql.append("         , mp.brand_id                  AS brandId                    ");
        sql.append("         , mp.all_parent_id             AS allParentId                ");
        sql.append("         , mp.sales_status_type         AS partStatus                 ");
        sql.append("         , mp.product_category_id       AS productCategoryId          ");
        sql.append("         , mp.pur_lot_size              AS purLotSize                 ");
        sql.append("      FROM mst_product mp                                             ");
        sql.append("         , mst_brand mb                                               ");
        sql.append("     WHERE mp.site_id = :siteId                                       ");
        sql.append("       AND mp.product_classification = :pc                            ");
        sql.append("       AND mb.site_id = mp.site_id                                    ");
        sql.append("       AND mb.brand_id = mp.brand_id                                  ");
        sql.append("    )                                                                 ");
        sql.append("    SELECT pl.brand                                AS brand           ");
        sql.append("         , pl.productId                            AS id              ");
        sql.append("         , pl.productCd                            AS code            ");
        sql.append("         , pl.productNm                            AS name            ");
        sql.append("         , CONCAT(pl.productCd, ' ', pl.productNm) AS desc            ");
        sql.append("         , pl.salLotSize                           AS salLotSize      ");
        sql.append("         , pl.stdRetailPrice                       AS stdRetailPrice  ");
        sql.append("         , pl.batteryFlag                          AS batteryFlag     ");
        sql.append("         , pl.largeGroup                           AS largeGroup      ");
        sql.append("         , pl.middlegroup                          AS middleGroup     ");
        sql.append("         , pl.productRetrieve                      AS productRetrieve ");
        sql.append("         , pl.brandId                              AS brandId         ");
        sql.append("         , pl.allParentId                          AS allParentId     ");
        sql.append("         , pl.partStatus                           AS partStatus      ");
        sql.append("         , pl.minPurQty                            AS minPurQty       ");
        sql.append("         , COALESCE(pc2.cost, pc.cost, 0)          AS price           ");
        sql.append("         , pl.purLotSize                           AS purLotSize      ");
        sql.append("      FROM p_list pl                                                  ");
        sql.append(" LEFT JOIN product_cost pc                                            ");
        sql.append("        ON pc.site_id = :ucSiteId                                     ");
        sql.append("       AND pc.product_id = pl.productId                               ");
        sql.append("       AND pc.cost_type = :costType                                   ");
        sql.append(" LEFT JOIN product_cost pc2                                           ");
        sql.append("        ON pc2.site_id = :ucSiteId                                    ");
        sql.append("       AND pc2.product_id = pl.productId                              ");
        sql.append("       AND pc2.cost_type = :costType2                                 ");
        sql.append("      WHERE 1 = 1                                                     ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("ucSiteId",model.getSiteId());
        params.put("pc", ProductClsType.PART.getCodeDbid());
        params.put("costType", CostType.RECEIVE_COST);
        params.put("costType2", CostType.AVERAGE_COST);

        if (StringUtils.isNotBlank(model.getBatteryFlag())) {
            sql.append(" AND pl.batteryFlag = :batteryFlag ");
            params.put("batteryFlag", model.getBatteryFlag());
        }

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND pl.productRetrieve LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        if (StringUtils.isNotBlank(model.getCode())) {
            sql.append(" AND pl.productRetrieve LIKE :productRetrieve ");
            params.put("productRetrieve", CommonConstants.CHAR_PERCENT + model.getCode().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }
        if (StringUtils.isNotBlank(model.getPartsCd())) {
            sql.append(" AND pl.productCd = :partCd ");
            params.put("partCd", model.getPartsCd());
        }

        if (StringUtils.isNotBlank(model.getBrandId())) {
            sql.append(" AND CAST(pl.brandId AS VARCHAR) = :brandId ");
            params.put("brandId", model.getBrandId());
        }

        if (model.getProdCtg() != null && !model.getProdCtg().isEmpty()) {
            if (model.getProdCtg().size() == 1) {
                sql.append(" AND split_part(pl.allParentId, '|', 1) = :largeGroupId ");
                params.put("largeGroupId", model.getProdCtg().get(0));
            } else if(model.getProdCtg().size() == 2) {
                sql.append(" AND split_part(pl.allParentId, '|', 1) = :largeGroupId ");
                sql.append(" AND split_part(pl.allParentId, '|', 2) = :middleGroupId ");
                params.put("largeGroupId", model.getProdCtg().get(0));
                params.put("middleGroupId", model.getProdCtg().get(1));
            }
        }

        if (StringUtils.isNotBlank(model.getLargeMiddleGroup())) {
            sql.append(" AND pl.allParentId like :largeMiddleGroup ");
            params.put("largeMiddleGroup", model.getLargeMiddleGroup().concat(CommonConstants.CHAR_PERCENT));
        }

        sql.append("  ORDER BY pl.productCd                            ");

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

        List<PartsVLBO> result = super.queryForList(sql.toString(), params, PartsVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public ValueListResultBO findServiceJobList(BaseVLForm model) {

        int pageSize = model.getPageSize();
        int currentPage = model.getCurrentPage();

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT product_id        AS id        ");
        sql.append("      , product_cd        AS code      ");
        sql.append("      , sales_description AS name      ");
        sql.append("      , concat(product_cd, ' ', sales_description)  AS desc      ");
        sql.append("   FROM mst_product mp                 ");
        sql.append("  WHERE 1 = 1                          ");
        sql.append("    AND site_id = :siteId              ");
        sql.append("    AND product_classification = :pc   ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("pc", ProductClsType.SERVICE.getCodeDbid());

        if (StringUtils.isNotBlank(model.getContent())) {
            sql.append(" AND REPLACE(upper( product_retrieve), ' ', '') LIKE :content ");
            params.put("content", CommonConstants.CHAR_PERCENT + model.getContent().replaceAll("\\s", "").toUpperCase() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("  ORDER BY product_cd                    ");

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

        List<BaseVLBO> result = super.queryForList(sql.toString(), params, BaseVLBO.class);

        return new ValueListResultBO(result, count);
    }

    @Override
    public Page<CMM050101BO> findPartsInformationInquiryAndPageList(CMM050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.brand_cd                                    AS brandCd            ");
        sql.append("          , mp.brand_id                                    AS brandId            ");
        sql.append("          , mp.site_id                                     AS siteId             ");
        sql.append("          , mp.product_cd                                  AS partsCd            ");
        sql.append("          , mp.sales_description                           AS partsNm            ");
        sql.append("          , mp.product_id                                  AS partsId            ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 1)                  AS largeGroupNm       ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 2)                  AS middleGroupNm      ");
        sql.append("          , mpc.product_category_id                        AS middleGroupId      ");
        sql.append("          , mp.registration_date                           AS registrationDate   ");
        sql.append("          , mp.std_retail_price                            AS priceExcludeVAT    ");
        sql.append("          , CASE WHEN sp1.parameter_value IS NOT NULL                            ");
        sql.append("                  AND sp1.parameter_value <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD')   ");
        sql.append("                 THEN ROUND((COALESCE(pt.tax_rate, CAST(sp2.parameter_value AS NUMERIC)) / 100 + 1) * mp.std_retail_price) ");
        sql.append("                 ELSE ROUND((CAST(sp2.parameter_value AS NUMERIC)/100 + 1) * mp.std_retail_price)    ");
        sql.append("                  END                                      AS priceIncludeVAT    ");
        sql.append("          , CASE WHEN mp.site_id = :siteIdN THEN ROUND(mp.std_retail_price * (1-COALESCE(cmo.discount_rate, 0))) ");
        sql.append("                 ELSE 0 END                                AS stdPurchasePrice   ");
        sql.append("       FROM mst_product mp                                                       ");
        sql.append("  LEFT JOIN cmm_mst_organization cmo                                             ");
        sql.append("         ON cmo.site_id = :siteIdN AND cmo.organization_cd = :siteId             ");
        sql.append("  LEFT JOIN mst_product_category mpc                                             ");
        sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 2)                    ");
        sql.append("        AND mpc.category_type = :middleGroup                                     ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxPeriod                       ");
        sql.append("                 AND site_id = :siteIdN   ) sp1 ON 1=1                            ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxRate                         ");
        sql.append("                 AND site_id = :siteIdN   ) sp2 ON 1=1                           ");
        sql.append("  LEFT JOIN product_tax pt                                                       ");
        sql.append("         ON pt.site_id = mp.site_id                                              ");
        sql.append("        AND pt.product_id = mp.product_id                                        ");
        sql.append("      WHERE mp.product_classification = :productClassification                   ");
        sql.append("        AND (mp.site_id = :siteId OR mp.site_id = :siteIdN)                      ");

        params.put("productClassification",ProductClsType.PART.getCodeDbid());
        params.put("siteId",siteId);
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("middleGroup",PJConstants.PartsCategory.MIDDLEGROUP);
        params.put("taxPeriod",MstCodeConstants.SystemParameterType.TAXPERIOD);
        params.put("taxRate",MstCodeConstants.SystemParameterType.TAXRATE);

        if (!Nulls.isNull(model.getParts())) {
            sql.append("    AND mp.product_retrieve LIKE :parts                                      ");
            params.put("parts", "%" + model.getParts() + "%");
        }

        if (!Nulls.isNull(model.getBrand())) {
            sql.append("    AND mp.brand_id = :brandId                                               ");
            params.put("brandId",model.getBrand());
        }

        if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_TWO) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(1));
        }else if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_ONE) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(0));
        }

        sql.append("  ORDER BY mp.brand_cd,mp.product_cd        ");

        Integer count = getCountForPartsInformationInquiry(model,siteId);
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, CMM050101BO.class, pageable), pageable, count);
    }

    private Integer getCountForPartsInformationInquiry(CMM050101Form model, String siteId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT COUNT(*)                                                                  ");
        sql.append("       FROM mst_product mp                                                       ");
        sql.append("  LEFT JOIN cmm_mst_organization cmo                                             ");
        sql.append("         ON cmo.site_id = :siteIdN AND cmo.organization_cd = :siteId             ");
        sql.append("  LEFT JOIN mst_product_category mpc                                             ");
        sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 2)                    ");
        sql.append("        AND mpc.category_type = :middleGroup                                     ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxPeriod                       ");
        sql.append("                 AND site_id = :siteId   ) sp1 ON 1=1                            ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxRate                         ");
        sql.append("                 AND site_id = :siteIdN   ) sp2 ON 1=1                           ");
        sql.append("  LEFT JOIN product_tax pt                                                       ");
        sql.append("         ON pt.site_id = mp.site_id                                              ");
        sql.append("        AND pt.product_id = mp.product_id                                        ");
        sql.append("      WHERE mp.product_classification = :productClassification                   ");
        sql.append("        AND (mp.site_id = :siteId OR mp.site_id = :siteIdN)                      ");

        params.put("productClassification",ProductClsType.PART.getCodeDbid());
        params.put("siteId",siteId);
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("middleGroup",PJConstants.PartsCategory.MIDDLEGROUP);
        params.put("taxPeriod",MstCodeConstants.SystemParameterType.TAXPERIOD);
        params.put("taxRate",MstCodeConstants.SystemParameterType.TAXRATE);

        if (!Nulls.isNull(model.getParts())) {
            sql.append("    AND mp.product_retrieve LIKE :parts                                      ");
            params.put("parts", "%" + model.getParts() + "%");
        }

        if (!Nulls.isNull(model.getBrand())) {
            sql.append("    AND mp.brand_id = :brandId                                               ");
            params.put("brandId",model.getBrand());
        }

        if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_TWO) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(1));
        }else if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_ONE) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(0));
        }

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<CMM050101BO> findPartsInformationInquiryList(CMM050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.brand_cd                                    AS brandCd            ");
        sql.append("          , mp.brand_id                                    AS brandId            ");
        sql.append("          , mp.site_id                                     AS siteId             ");
        sql.append("          , mp.product_cd                                  AS partsCd            ");
        sql.append("          , mp.sales_description                           AS partsNm            ");
        sql.append("          , mp.product_id                                  AS partsId            ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 1)                  AS largeGroupNm       ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 2)                  AS middleGroupNm      ");
        sql.append("          , mpc.product_category_id                        AS middleGroupId      ");
        sql.append("          , mp.registration_date                           AS registrationDate   ");
        sql.append("          , mp.std_retail_price                            AS priceExcludeVAT    ");
        sql.append("          , CASE WHEN sp1.parameter_value IS NOT NULL                            ");
        sql.append("                  AND sp1.parameter_value <= TO_CHAR(CURRENT_DATE, 'YYYYMMDD')   ");
        sql.append("                 THEN ROUND((pt.tax_rate/100 + 1) * mp.std_retail_price)         ");
        sql.append("                 ELSE ROUND((CAST(sp2.parameter_value AS NUMERIC)/100 + 1) * mp.std_retail_price)    ");
        sql.append("                  END                                      AS priceIncludeVAT    ");
        sql.append("          , CASE WHEN mp.site_id = :siteIdN THEN ROUND(mp.std_retail_price * (1-COALESCE(cmo.discount_rate, 0))) ");
        sql.append("                 ELSE 0 END                                AS stdPurchasePrice   ");
        sql.append("       FROM mst_product mp                                                       ");
        sql.append("  LEFT JOIN cmm_mst_organization cmo                                             ");
        sql.append("         ON cmo.site_id = :siteIdN AND cmo.organization_cd = :siteId             ");
        sql.append("  LEFT JOIN mst_product_category mpc                                             ");
        sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 2)                    ");
        sql.append("        AND mpc.category_type = :middleGroup                                     ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxPeriod                       ");
        sql.append("                 AND site_id = :siteId   ) sp1 ON 1=1                            ");
        sql.append("  LEFT JOIN ( SELECT parameter_value                                             ");
        sql.append("                FROM system_parameter                                            ");
        sql.append("               WHERE system_parameter_type_id = :taxRate                         ");
        sql.append("                 AND site_id = :siteIdN   ) sp2 ON 1=1                           ");
        sql.append("  LEFT JOIN product_tax pt                                                       ");
        sql.append("         ON pt.site_id = mp.site_id                                              ");
        sql.append("        AND pt.product_id = mp.product_id                                        ");
        sql.append("      WHERE mp.product_classification = :productClassification                   ");
        sql.append("        AND (mp.site_id = :siteId OR mp.site_id = :siteIdN)                      ");

        params.put("productClassification",ProductClsType.PART.getCodeDbid());
        params.put("siteId",siteId);
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("middleGroup",PJConstants.PartsCategory.MIDDLEGROUP);
        params.put("taxPeriod",MstCodeConstants.SystemParameterType.TAXPERIOD);
        params.put("taxRate",MstCodeConstants.SystemParameterType.TAXRATE);

        if (!Nulls.isNull(model.getParts())) {
            sql.append("    AND mp.product_retrieve LIKE :parts                                      ");
            params.put("parts", "%" + model.getParts() + "%");
        }

        if (!Nulls.isNull(model.getBrand())) {
            sql.append("    AND mp.brand_id = :brandId                                               ");
            params.put("brandId",model.getBrand());
        }

        if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_TWO) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(1));
        }else if (model.getProductCategory()!=null && model.getProductCategory().size() == CommonConstants.INTEGER_ONE) {
            sql.append("AND mp.all_path LIKE (SELECT mpc.all_path FROM mst_product_category mpc WHERE mpc.product_category_id = :category) || '%'");
            params.put("category",model.getProductCategory().get(0));
        }

        sql.append("  ORDER BY mp.brand_cd,mp.product_cd        ");

        return super.queryForList(sql.toString(), params, CMM050101BO.class);
    }

    //查找grid内容
    @Override
    public List<CMM050102PurchaseControlBO> getPurchaseControlGridAddList(CMM050102Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_cd            AS pointCd                       ");
        sql.append("          , mf.facility_nm            AS pointNm                       ");
        sql.append("          , mf.facility_id            AS pointId                       ");
        sql.append("       FROM mst_facility mf                                            ");
        sql.append("      WHERE mf.site_id = :siteId                                       ");
        sql.append("   ORDER BY mf.facility_cd                                             ");
        params.put("siteId",siteId);

        return super.queryForList(sql.toString(), params, CMM050102PurchaseControlBO.class);
    }

    @Override
    public List<CMM050102PurchaseControlBO> getPurchaseControlGridEditList(CMM050102Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_cd            AS pointCd                       ");
        sql.append("          , mf.facility_nm            AS pointNm                       ");
        sql.append("          , mf.facility_id            AS pointId                       ");
        sql.append("          , l.location_cd             AS mainLocation                  ");
        sql.append("          , pi2.location_id           AS locationId                    ");
        sql.append("          , rg.rop_roq_manual_flag    AS ropAndRoqSign                 ");
        sql.append("          , rg.reorder_point          AS rop                           ");
        sql.append("          , rg.reorder_qty            AS roq                           ");
        sql.append("          , pai.abc_type              AS costUsage                     ");
        sql.append("       FROM mst_facility mf                                            ");
        sql.append("  LEFT JOIN product_inventory pi2                                      ");
        sql.append("         ON pi2.facility_id = mf.facility_id                           ");
        sql.append("        AND pi2.site_id = mf.site_id                                   ");
        sql.append("        AND pi2.primary_flag = :charYES                                ");
        sql.append("        AND pi2.product_id = :productId                                ");
        sql.append("  LEFT JOIN location l                                                 ");
        sql.append("         ON pi2.location_id = l.location_id                            ");
        sql.append("        AND l.site_id = mf.site_id                                     ");
        sql.append("        AND l.primary_flag = :charYES                                  ");
        sql.append("        AND l.facility_id = mf.facility_id                             ");
        sql.append("  LEFT JOIN reorder_guideline rg                                       ");
        sql.append("         ON rg.site_id = mf.site_id                                   ");
        sql.append("        AND rg.facility_id = mf.facility_id                           ");
        sql.append("        AND rg.product_id = :productId                                 ");
        sql.append("  LEFT JOIN product_abc_info pai                                       ");
        sql.append("         ON pai.site_id = mf.site_id                                  ");
        sql.append("        AND pai.facility_id = mf.facility_id                          ");
        sql.append("        AND pai.product_id = :productId                                ");
        sql.append("      WHERE mf.site_id = :siteId                                       ");
        sql.append("   ORDER BY mf.facility_cd                                             ");

        params.put("siteId",siteId);
        params.put("productId",model.getPartsId());
        params.put("charYES",CommonConstants.CHAR_Y);

        return super.queryForList(sql.toString(), params, CMM050102PurchaseControlBO.class);
    }

    @Override
    public CMM050102BasicInfoBO getBasicInfoList(CMM050102Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_id                          AS partsId               ");
        sql.append("          , mp.product_cd                          AS partsNo               ");
        sql.append("          , mp.english_description                 AS partsNm               ");
        sql.append("          , mp.local_description                   AS localNm               ");
        sql.append("          , mp.sales_description                   AS salesNm               ");
        sql.append("          , mp.registration_date                   AS registrationDate      ");
        sql.append("          , supersedingParts                                                ");
        sql.append("          , supersedingPartsId                                              ");
        sql.append("          , supersedingPartsNm                                              ");
        sql.append("          , mp.product_property ->> 'length'       AS length                ");
        sql.append("          , mp.product_property ->> 'width'        AS width                 ");
        sql.append("          , mp.product_property ->> 'height'       AS height                ");
        sql.append("          , mp.product_property ->> 'weight'       AS weight                ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 2)          AS middleGroup           ");
        sql.append("          , SPLIT_PART(mp.all_path, '|', 2)        AS middleGroupCd         ");
        sql.append("          , SPLIT_PART(mp.all_parent_id , '|', 2)  AS middleGroupId         ");
        sql.append("       FROM mst_product mp                                                  ");
        sql.append("  LEFT JOIN (                                                               ");
        sql.append("         SELECT CASE WHEN new_part.product_cd IS NULL        THEN '' ELSE new_part.product_cd END AS supersedingParts            ");
        sql.append("              , CASE WHEN new_part.product_id IS NULL        THEN NULL ELSE new_part.product_id END AS supersedingPartsId        ");
        sql.append("              , CASE WHEN new_part.sales_description IS NULL THEN '' ELSE new_part.sales_description END AS supersedingPartsNm   ");
        sql.append("           FROM mst_product_relation mpr                                    ");
        sql.append("      LEFT JOIN mst_product AS new_part                                     ");
        sql.append("             ON mpr.from_product_id = new_part.product_id                   ");
        sql.append("          WHERE mpr.site_id = :siteIdN                                      ");
        sql.append("            AND mpr.relation_type = :supplierCd                             ");
        sql.append("            AND mpr.to_product_id = :productId                              ");
        sql.append("              ) subquery ON true                                            ");
        sql.append("      WHERE mp.product_classification = :productClassification              ");
        sql.append("        AND mp.product_id = :productId                                      ");
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("supplierCd",PJConstants.ProductRelationClass.SUPERSEDING);
        params.put("productClassification",ProductClsType.PART.getCodeDbid());
        params.put("productId",model.getPartsId());

        return super.queryForSingle(sql.toString(), params, CMM050102BasicInfoBO.class);
    }


    @Override
    public CMM050102SalesControlBO getSaleControlList(CMM050102Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_id                      AS partsId         ");
        sql.append("          , mp.product_cd                      AS partsNo         ");
        sql.append("          , mp.english_description             AS partsNm         ");
        sql.append("          , mp.std_retail_price                AS stdRetailPrice  ");
        sql.append("          , mp.std_price_update_date           AS update1         ");
        sql.append("          , COALESCE(pcavg.cost, 0)            AS averageCost     ");
        sql.append("          , pcavg.last_updated                 AS update2         ");
        sql.append("          , pcrec.cost                         AS lastCost        ");
        sql.append("          , pcrec.last_updated                 AS update3         ");
        sql.append("          , mp.sal_lot_size                    AS salesLot        ");
        sql.append("          , mp.min_sal_qty                     AS qty             ");
        sql.append("          , mp.sales_status_type               AS unavailable     ");
        sql.append("          , mp.brand_cd                        AS brandCd         ");
        sql.append("          , mp.pur_lot_size                    AS purchaseLot     ");
        sql.append("          , mp.min_pur_qty                     AS purchaseQty     ");
        sql.append("          , ROUND(mp.std_retail_price * (1-COALESCE(cmo.discount_rate, 0))) AS stdPurchasePrice");
        sql.append("       FROM mst_product mp                                        ");
        sql.append("  LEFT JOIN product_cost pcavg                                    ");
        sql.append("         ON mp.product_id = pcavg.product_id                      ");
        sql.append("        AND pcavg.site_id = :siteId                               ");
        sql.append("        AND pcavg.cost_type = :averageCost                        ");
        sql.append("  LEFT JOIN product_cost pcrec                                    ");
        sql.append("         ON mp.product_id = pcrec.product_id                      ");
        sql.append("        AND pcrec.site_id = :siteId                               ");
        sql.append("        AND pcrec.cost_type = :receiveCost                        ");
        sql.append("  LEFT JOIN cmm_mst_organization cmo                              ");
        sql.append("         ON cmo.site_id = :siteIdN                                ");
        sql.append("        AND cmo.organization_cd = :siteId                         ");
        sql.append("      WHERE mp.product_id = :productId                            ");

        params.put("siteId",siteId !=null ?siteId:model.getSiteId());
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("averageCost",PJConstants.CostType.AVERAGE_COST);
        params.put("receiveCost",PJConstants.CostType.RECEIVE_COST);
        params.put("productId",model.getPartsId());

        return super.queryForSingle(sql.toString(), params, CMM050102SalesControlBO.class);
    }

    @Override
    public List<CMM060101BO> getSvJobData(CMM060101Form model) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT product_cd           AS serviceJob               ");
        sql.append("         , local_description    AS serviceJobName           ");
        sql.append("         , english_description  AS englishName              ");
        sql.append("         , sales_description    AS salesName                ");
        sql.append("      FROM mst_product                                      ");

        sql.append("     WHERE site_id = :siteId                                ");

        if(StringUtils.isNotBlank(model.getServiceJob())) {
            sql.append("   AND product_cd like :serviceJob                      ");
            params.put("serviceJob", CommonConstants.CHAR_PERCENT + model.getServiceJob() + CommonConstants.CHAR_PERCENT);
        }

        if(StringUtils.isNotBlank(model.getServiceJobName())) {
            sql.append("   AND local_description like :serviceJobName           ");
            params.put("serviceJobName", CommonConstants.CHAR_PERCENT + model.getServiceJobName() + CommonConstants.CHAR_PERCENT);
        }

        sql.append("       AND product_classification = :productCls             ");
        sql.append("  ORDER BY product_cd                                       ");

        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productCls", ProductClsType.SERVICE.getCodeDbid());

        return super.queryForList(sql.toString(), params, CMM060101BO.class);
    }

    @Override
    public List<PartsDeadStockItemBO> getAllNoneNewAndHasStockParts(String siteId,Long facilityId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        Set<String> statusTypes = Set.of(  SpStockStatus.ONHAND_QTY.getCodeDbid()
                                        , SpStockStatus.ONRECEIVING_QTY.getCodeDbid()
                                        , SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid()
                                        , SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid()
                                        , SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid()
                                        , SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid()
                                        , SpStockStatus.BO_QTY.getCodeDbid());

        sql.append("     select       p.product_id  as  productId               ");
        sql.append("                , p.product_cd  as  productCd               ");
        sql.append("                , s.product_stock_status_type as productStockStatusTypeDbId           ");
        sql.append("                , s.quantity as quantity                    ");
        sql.append("                , di.abc_type as abcType                    ");
        sql.append("       from product_stock_status s                          ");
        sql.append("  left join mst_product p on s.product_id = p.product_id    ");
        sql.append("  left join product_abc_info pabci on pabci.product_id = p.product_id");
        sql.append("        and pabci.site_id = :siteId                         ");
        sql.append("        and pabci.facility_id = :facId                      ");
        sql.append("  left join abc_definition_info di on di.abc_definition_info_id = pabci.abc_definition_id");
        sql.append(" where                                                      ");
        sql.append("       s.quantity > 0                                       "); //Has some stock status.
        sql.append("   and s.product_stock_status_type in (:statusTypes)        ");
        sql.append("   and s.facility_id = :facId                               ");
        sql.append("   and s.site_id = :siteId                                  ");
        sql.append("   and                                                      ");
        sql.append("   not exists(                                              "); //It's the old product.
        sql.append("            select 1 from product_abc_info pabci2           ");
        sql.append("              left join abc_definition_info di2 on di2.abc_definition_info_id = pabci2.abc_definition_id");
        sql.append("                    where pabci2.product_id=p.product_id    ");
        sql.append("                      and di2.abc_type like 'N%'             ");
        sql.append("                      and pabci2.facility_id = :facId       ");
        sql.append("                      and pabci2.site_id = :siteId          ");
        sql.append("            )                                               ");
        sql.append("   and p.product_classification = :partsType                ");//It's parts.
        sql.append("   and p.site_id=:defaultSite                               ");
        sql.append(" order by p.product_id                                      ");

        params.put("siteId", siteId);
        params.put("facId", facilityId);
        params.put("partsType", ProductClsType.PART.getCodeDbid());
        params.put("statusTypes", statusTypes);
        params.put("defaultSite", CommonConstants.CHAR_DEFAULT_SITE_ID);

        return super.queryForList(sql.toString(), params, PartsDeadStockItemBO.class);
    }

    @Override
    public List<PartsDeadStockItemBO> getAllJ1TotalAndAvgDemandOnParts(String siteId,Long facilityId,Set<Long> partsIds) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     select       f.product_id    as  productId                 ");
        sql.append("                , f.string_value  as  stringValue               ");
        sql.append("                , f.ropq_type     as  ropqType                  ");
        sql.append("       from parts_ropq_monthly f                                ");
        sql.append("  where                                                         ");
        sql.append("  (f.string_value is not null and f.string_value != '0')        ");
        sql.append("   and                                                          ");
        sql.append("       (f.ropq_type = :j1Cate or f.ropq_type = :avgDemandCate)                  ");
        sql.append("  and f.product_id in (");
        sql.append("            select p.product_id                                                     ");
        sql.append("              from mst_product p                                      "); //Has some stock status.
        sql.append("             where p.site_id = :defaultSite       ");
        sql.append("          and not exists(                                              "); //It's the old product.
        sql.append("            select 1 from product_abc_info pabci2           ");
        sql.append("              left join abc_definition_info di2 on di2.abc_definition_info_id = pabci2.abc_definition_id");
        sql.append("                    where pabci2.product_id=p.product_id    ");
        sql.append("                      and di2.abc_type like 'N%'             ");
        sql.append("                      and pabci2.facility_id = :facId       ");
        sql.append("                      and pabci2.site_id = :siteId          ");
        sql.append("            )                                               ");
        sql.append("          and p.product_classification = :partsType                ");//It's parts.
        sql.append("          and p.product_id in (:partsIds)                ");//It's parts.
        sql.append("   )                ");//It's parts.
        sql.append("   and f.facility_id = :facId                                     ");
        sql.append("   and f.site_id = :siteId                                     ");
        sql.append(" order by f.product_id");

        params.put("siteId", siteId);
        params.put("facId", facilityId);
        params.put("partsType", ProductClsType.PART.getCodeDbid());
        params.put("partsIds", partsIds);
        params.put("j1Cate", RopRoqParameter.KEY_PARTSJ1TOTAL);
        params.put("avgDemandCate", RopRoqParameter.KEY_PARTSAVERAGEDEMAND);
        params.put("defaultSite", CommonConstants.CHAR_DEFAULT_SITE_ID);


        return super.queryForList(sql.toString(), params, PartsDeadStockItemBO.class);
    }

    @Override
    public CMQ050101InformationBO findPartsInformationReportList(CMQ050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.english_description                     AS partsNm             ");
        sql.append("          , mp.local_description                       AS localNm             ");
        sql.append("          , mp.sales_description                       AS salesNm             ");
        sql.append("          , mp.registration_date                       AS registrationDate    ");
        sql.append("          , SPLIT_PART(mp.all_nm, '|', 2)              AS middleGroup         ");
        sql.append("          , SPLIT_PART(mp.all_path, '|', 2)            AS middleGroupCd       ");
        sql.append("          , SPLIT_PART(mp.all_parent_id , '|', 2)      AS middleGroupId       ");
        sql.append("          , SPLIT_PART(mp.all_parent_id , '|', 1)      AS largeGroupId        ");
        sql.append("          , mp.product_property ->> 'length'           AS length              ");
        sql.append("          , mp.product_property ->> 'width'            AS width               ");
        sql.append("          , mp.product_property ->> 'height'           AS height              ");
        sql.append("          , mp.product_property ->> 'weight'           AS weight              ");
        sql.append("          , mp.site_id                                 AS siteId              ");
        sql.append("          , mp.sal_lot_size                            AS salesLot            ");
        sql.append("          , mp.min_sal_qty                             AS minSalesQty         ");
        sql.append("          , mp.sales_status_type                       AS unavailable         ");
        sql.append("          , mp.brand_id                                AS brandId             ");
        sql.append("          , mp.brand_cd                                AS brandCd             ");
        sql.append("          , mb.brand_nm                                AS brandNm             ");
        sql.append("          , mp.pur_lot_size                            AS purchaseLot         ");
        sql.append("          , mp.min_pur_qty                             AS minPurchaseQty      ");
        sql.append("          , newmp.product_cd                           AS supersedingParts    ");
        sql.append("          , newmp.sales_description                    AS supersedingPartsNm  ");
        sql.append("          , newmp.product_id                           AS supersedingPartsId  ");
        sql.append("       FROM mst_product mp                                                    ");
        sql.append("  LEFT JOIN mst_product_relation mpr                                          ");
        sql.append("         ON mp.product_id = mpr.to_product_id                                 ");
        sql.append("  LEFT JOIN mst_product newmp                                                 ");
        sql.append("         ON mpr.from_product_id = newmp.product_id                            ");
        sql.append("        AND mpr.site_id = :siteIdN                                            ");
        sql.append("        AND mpr.relation_type = :superseding                                  ");
        sql.append("  LEFT JOIN mst_brand mb                                                      ");
        sql.append("         ON mb.site_id = :siteIdN                                             ");
        sql.append("        AND mb.brand_id = mp.brand_id                                         ");
        sql.append("      WHERE mp.product_id = :productId                                        ");
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("superseding",ProductRelationClass.SUPERSEDING);
        params.put("productId",model.getPartsId());

        return super.queryForSingle(sql.toString(), params, CMQ050101InformationBO.class);
    }

    @Override
    public List<CMQ050101BasicInfoBO> findBasicInfoList(CMQ050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mp.product_cd                      AS supersededPartsNo                 ");
        sql.append("          , mp.sales_description               AS supersededPartsNm                 ");
        sql.append("          , mp.product_id                      AS supersededPartsId                 ");
        sql.append("       FROM mst_product_relation mpr                                                ");
        sql.append("  LEFT JOIN mst_product mp                                                          ");
        sql.append("         ON mpr.to_product_id = mp.product_id                                       ");
        sql.append("      WHERE mpr.site_id = :siteIdN                                                  ");
        sql.append("        AND mpr.relation_type  = :superseding                                       ");
        sql.append("        AND mpr.from_product_id  = :productId                                       ");
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("superseding",ProductRelationClass.SUPERSEDING);
        params.put("productId",model.getPartsId());

        return super.queryForList(sql.toString(), params, CMQ050101BasicInfoBO.class);
    }

    @Override
    public List<CMQ050101PurchaseControlBO> findPurchaseControlList(CMQ050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_nm                             AS pointNm                   ");
        sql.append("          , mf.facility_id                             AS pointId                   ");
        sql.append("          , mf.facility_cd                             AS pointCd                   ");
        sql.append("          , l.location_cd                              AS mainLocation              ");
        sql.append("          , pi2.location_id                            AS locationId                ");
        sql.append("          , COALESCE(rg.rop_roq_manual_flag, :charN)   AS manualROPROQSign          ");
        sql.append("          , rg.reorder_point                           AS rop                       ");
        sql.append("          , rg.reorder_qty                             AS roq                       ");
        sql.append("          , pai.abc_type                               AS costUsage                 ");
        sql.append("          , COALESCE(sim.manual_flag, :charN)          AS seasonIndexManualSign     ");
        sql.append("       FROM mst_facility mf                                                         ");
        sql.append("  LEFT JOIN product_inventory pi2                                                   ");
        sql.append("         ON pi2.site_id  = :siteId                                                  ");
        sql.append("        AND pi2.facility_id = mf.facility_id                                        ");
        sql.append("        AND pi2.primary_flag = :charY                                               ");
        sql.append("        AND pi2.product_id = :productId                                             ");
        sql.append("  LEFT JOIN location l                                                              ");
        sql.append("         ON pi2.location_id = l.location_id                                         ");
        sql.append("  LEFT JOIN reorder_guideline rg                                                    ");
        sql.append("         ON rg.site_id  = :siteId                                                   ");
        sql.append("        AND rg.facility_id = mf.facility_id                                         ");
        sql.append("        AND rg.product_id IN (SELECT mp2.product_id                                 ");
        sql.append("                               FROM mst_product_relation mpr                        ");
        sql.append("                          LEFT JOIN mst_product  mp2                                ");
        sql.append("                                 ON mpr.to_product_id = mp2.product_id              ");
        sql.append("                              WHERE mpr.site_id = :siteIdN                          ");
        sql.append("                                AND mpr.relation_type = :superseding                ");
        sql.append("                                AND mpr.from_product_id = :productId   )            ");
        sql.append("  LEFT JOIN product_abc_info pai                                                    ");
        sql.append("         ON pai.site_id = :siteId                                                   ");
        sql.append("        AND pai.facility_id = mf.facility_id                                        ");
        sql.append("        AND pai.product_id = :productId                                             ");
        sql.append("  LEFT JOIN season_index_manual sim                                                 ");
        sql.append("         ON sim.site_id = :siteId                                                   ");
        sql.append("        AND sim.facility_id = mf.facility_id                                        ");
        sql.append("        AND CAST(sim.product_category_id AS VARCHAR) = (                            ");
        sql.append("                          SELECT SPLIT_PART(mp.all_parent_id , '|', 1)              ");
        sql.append("                            FROM mst_product mp                                     ");
        sql.append("                           WHERE mp.site_id = :siteId                               ");
        sql.append("                             AND mp.product_id =:productId )                        ");
        sql.append("      WHERE mf.site_id = :siteId                                                    ");
        sql.append("   ORDER BY mf.facility_cd                                                          ");

        params.put("siteId",siteId);
        params.put("charN",CommonConstants.CHAR_N);
        params.put("charY",CommonConstants.CHAR_Y);
        params.put("siteIdN",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("superseding",ProductRelationClass.SUPERSEDING);
        params.put("productId",model.getPartsId());

        return super.queryForList(sql.toString(), params, CMQ050101PurchaseControlBO.class);
    }

    @Override
    public List<CMQ050101StockInfoBO> findStockInfoList(CMQ050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_nm                    AS pointNm                       ");
        sql.append("          , mf.facility_id                    AS pointId                       ");
        sql.append("          , mf.facility_cd                    AS pointCd                       ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onHandQty           ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onHandQty                   ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :boQty               ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS backOrder                   ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :allocatiedQty       ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS allocatedQty                ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type                        ");
        sql.append("                   IN (:hoonPurchaseQty, :eoOnpurchaseQty, :roOnpurchaseQty, :woonPurchaseQty)         ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onPurchaseQty               ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onreceivingQty      ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onReceivingQty              ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onfrozenQty         ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onFrozenQty                 ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onpickingQty        ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onPickingQty                ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :ontransferInQty     ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS onTransferQty               ");
        sql.append("          , SUM(CASE WHEN pss.product_stock_status_type                        ");
        sql.append("                   IN (:onHandQty, :allocatiedQty, :onpickingQty, :ontransferInQty, :onfrozenQty)       ");
        sql.append("                 THEN pss.quantity ELSE 0 END ) AS stockQty                    ");
        sql.append("       FROM mst_facility mf                                                    ");
        sql.append("  LEFT JOIN product_stock_status pss                                           ");
        sql.append("         ON pss.site_id = :siteId                                              ");
        sql.append("        AND pss.facility_id = mf.facility_id                                   ");
        sql.append("        AND pss.product_id = :productId                                        ");
        sql.append("      WHERE mf.site_id  = :siteId                                              ");
        sql.append("   GROUP BY mf.facility_cd,mf.facility_nm,mf.facility_id                       ");
        sql.append("   ORDER BY mf.facility_cd                                                     ");

        params.put("siteId",siteId);
        params.put("productId",model.getPartsId());
        params.put("onHandQty",PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("boQty",PJConstants.SpStockStatus.BO_QTY.getCodeDbid());
        params.put("allocatiedQty",PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        params.put("hoonPurchaseQty",PJConstants.SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        params.put("eoOnpurchaseQty",PJConstants.SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        params.put("roOnpurchaseQty",PJConstants.SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        params.put("woonPurchaseQty",PJConstants.SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid());
        params.put("onreceivingQty",PJConstants.SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        params.put("onfrozenQty",PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("onpickingQty",PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid());
        params.put("ontransferInQty",PJConstants.SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());

        return super.queryForList(sql.toString(), params, CMQ050101StockInfoBO.class);
    }

    @Override
    public List<CMQ050101DemandDetailBO> findDemandList(CMQ050101Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_nm                      AS pointNm                     ");
        sql.append("          , mf.facility_id                      AS pointId                     ");
        sql.append("          , mf.facility_cd                      AS pointCd                     ");
        sql.append("          , COALESCE(prp.first_order_date, '')  AS firstOrderDay               ");
        sql.append("          , CASE WHEN prm1.ropq_type = :jOnePartsTotal THEN prm1.string_value ELSE '' END AS jOne  ");
        sql.append("          , CASE WHEN prm2.ropq_type = :jTwoPartsTotal THEN prm2.string_value ELSE '' END AS jTwo  ");
        sql.append("       FROM mst_facility mf                                                    ");
        sql.append("  LEFT JOIN parts_ropq_parameter prp                                           ");
        sql.append("         ON prp.site_id  = :siteId                                             ");
        sql.append("        AND prp.facility_id  = mf.facility_id                                  ");
        sql.append("        AND prp.product_id  = :productId                                       ");
        sql.append("  LEFT JOIN parts_ropq_monthly prm1                                            ");
        sql.append("         ON prm1.site_id = :siteId                                             ");
        sql.append("        AND prm1.facility_id = mf.facility_id                                  ");
        sql.append("        AND prm1.product_id = :productId                                       ");
        sql.append("        AND prm1.ropq_type = :jOnePartsTotal                                   ");
        sql.append("  LEFT JOIN parts_ropq_monthly prm2                                            ");
        sql.append("         ON prm2.site_id = :siteId                                             ");
        sql.append("        AND prm2.facility_id = mf.facility_id                                  ");
        sql.append("        AND prm2.product_id = :productId                                       ");
        sql.append("        AND prm2.ropq_type = :jTwoPartsTotal                                   ");
        sql.append("      WHERE mf.site_id = :siteId                                               ");
        sql.append("   ORDER BY mf.facility_cd                                                     ");

        params.put("siteId",siteId);
        params.put("productId",model.getPartsId());
        params.put("jOnePartsTotal",PJConstants.RopRoqParameter.KEY_PARTSJ1TOTAL);
        params.put("jTwoPartsTotal",PJConstants.RopRoqParameter.KEY_PARTSJ2TOTAL);

        return super.queryForList(sql.toString(), params, CMQ050101DemandDetailBO.class);
    }

    @Override
    public List<SPM040601BO> getMstProductPriceList(List<String> partsNoList, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH p_list AS (                                            ");
        sql.append("    SELECT mp.product_id        AS partsId                  ");
        sql.append("         , mp.product_cd        AS partsNo                  ");
        sql.append("         , mp.sales_description AS partsNm                  ");
        sql.append("         , mp.std_retail_price  AS stdRetailPrice           ");
        sql.append("      FROM mst_product mp                                   ");
        sql.append("     WHERE mp.site_id = :siteId1                            ");
        sql.append("       AND mp.product_classification = :pc                  ");
        sql.append("       AND mp.product_cd IN (:partsNoList)                  ");
        sql.append(" UNION ALL                                                  ");
        sql.append("    SELECT mp.product_id AS partsId                         ");
        sql.append("         , mp.product_cd AS partsNo                         ");
        sql.append("         , mp.sales_description AS partsNm                  ");
        sql.append("         , mp.std_retail_price AS stdRetailPrice            ");
        sql.append("    FROM mst_product mp                                     ");
        sql.append("   WHERE mp.site_id = :siteId2                              ");
        sql.append("     AND mp.product_classification = :pc                    ");
        sql.append("     AND mp.product_cd IN (:partsNoList)                    ");
        sql.append("    )                                                       ");
        sql.append("    SELECT pl.partsId                     AS partsId        ");
        sql.append("         , pl.partsNo                     AS partsNo        ");
        sql.append("         , pl.partsNm                     AS partsNm        ");
        sql.append("         , pl.stdRetailPrice              AS stdRetailPrice ");
        sql.append("         , COALESCE(pc.cost, pc2.cost, 0) AS price          ");
        sql.append("      FROM p_list pl                                        ");
        sql.append(" LEFT JOIN product_cost pc                                  ");
        sql.append("        ON pc.site_id = :siteId2                            ");
        sql.append("       AND pc.product_id = pl.partsId                       ");
        sql.append("       AND pc.cost_type = :costType                         ");
        sql.append(" LEFT JOIN product_cost pc2                                 ");
        sql.append("        ON pc2.site_id = :siteId2                           ");
        sql.append("       AND pc2.product_id = pl.partsId                      ");
        sql.append("       AND pc2.cost_type = :costType2                       ");
        sql.append("      WHERE 1 = 1                                           ");

        params.put("siteId1", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("partsNoList", partsNoList);
        params.put("siteId2", siteId);
        params.put("pc", ProductClsType.PART.getCodeDbid());
        params.put("costType", CostType.RECEIVE_COST);
        params.put("costType2", CostType.AVERAGE_COST);

        return super.queryForList(sql.toString(), params, SPM040601BO.class);
    }

    @Override
    public Page<CMQ050801BO> findPartsSummaryList(CMQ050801Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if(!PJConstants.DemandSource.ORIGINALDEMAND.equals(model.getDemandSource())) {
            sql.append("     SELECT mp.product_cd                                                                        AS partsCd          ");
            sql.append("          , mp.sales_description                                                                 AS partsNm          ");
            sql.append("          , mp.product_id                                                                        AS partsId          ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1)                                                      AS largeGroupCd     ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1) || ' ' || SPLIT_PART(mp.all_nm, '|', 1)              AS largeGroupAbbr   ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2)                                                      AS middleGroupCd    ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2) || ' ' || SPLIT_PART(mp.all_nm, '|', 2)              AS middleGroupAbbr  ");
            sql.append("          , pai.abc_type                                                                         AS costUsage        ");
            sql.append("          , :current                                                                             AS demandSource     ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jOnePartsTotal  THEN prm.string_value ELSE NULL END)  AS jOne             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jTwoPartsTotal  THEN prm.string_value ELSE NULL END)  AS jTwo             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :partsTrendIndex THEN prm.string_value ELSE NULL END ) AS trendIndex       ");
            sql.append("          , df.demand_qty                                                                        AS demandForecast   ");
            sql.append("          , rg.reorder_point                                                                     AS rop              ");
            sql.append("          , rg.reorder_qty                                                                       AS roq              ");
            sql.append("          , rg.rop_roq_manual_flag                                                               AS manualSign       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :boQty  THEN pss.quantity ELSE 0 END)       AS boQty       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onHandQty  THEN pss.quantity ELSE 0 END)   AS onHandQty   ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in(:onHandQty,:allocatiedQty,:onpickingQty,:ontransferInQty,:onShipping,:onCanvassInQty,:onServiceQty,:onfrozenQty,:onBorrowingQty)         ");
            sql.append("                           THEN pss.quantity ELSE 0 END) AS totalStock                                               ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in (:onHandQty,:ontransferInQty,:onreceivingQty,:eoOnpurchaseQty,:hoonPurchaseQty,:roOnPurchaseQty)             ");
            sql.append("                              THEN pss.quantity ELSE 0 END) AS futureStock                                           ");
            sql.append("          , COALESCE(sib.n_index,                                                                                    ");
            sql.append("                       CASE WHEN to_char(current_date, 'MM') = '01' THEN sim.index_month01                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '02' THEN sim.index_month02                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '03' THEN sim.index_month03                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '04' THEN sim.index_month04                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '05' THEN sim.index_month05                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '06' THEN sim.index_month06                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '07' THEN sim.index_month07                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '08' THEN sim.index_month08                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '09' THEN sim.index_month09                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '10' THEN sim.index_month10                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '11' THEN sim.index_month11                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '12' THEN sim.index_month12                           ");
            sql.append("                        ELSE 0 END, 0 ) AS seasonIndex                                                               ");
            sql.append("       FROM mst_product mp                                                           ");
            sql.append("  LEFT JOIN mst_product_category mpc                                                 ");
            sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 1)                        ");
            sql.append("        AND mpc.site_id  = mp.site_id                                                ");
            sql.append("  LEFT JOIN product_abc_info pai                                                     ");
            sql.append("         ON pai.site_id = :siteId                                                    ");
            sql.append("        AND pai.facility_id = :facilityId                                            ");
            sql.append("        AND pai.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN parts_ropq_monthly prm                                                   ");
            sql.append("         ON prm.site_id = :siteId                                                    ");
            sql.append("        AND prm.facility_id = :facilityId                                            ");
            sql.append("        AND prm.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN demand_forecast df                                                       ");
            sql.append("         ON df.site_id = :siteId                                                     ");
            sql.append("        AND df.facility_id  =:facilityId                                             ");
            sql.append("        AND df.to_product_id  = mp.product_id                                        ");
            sql.append("        AND df.target_month = TO_CHAR(CURRENT_DATE, :yearAndMonth)                   ");
            sql.append("  LEFT JOIN reorder_guideline rg                                                     ");
            sql.append("         ON rg.site_id = :siteId                                                     ");
            sql.append("        AND rg.facility_id  =:facilityId                                             ");
            sql.append("        AND rg.product_id  = mp.product_id                                           ");
            sql.append("  LEFT JOIN product_stock_status pss                                                 ");
            sql.append("         ON pss.site_id = :siteId                                                    ");
            sql.append("        AND pss.facility_id  =:facilityId                                            ");
            sql.append("        AND pss.product_id  = mp.product_id                                          ");
            sql.append("  LEFT JOIN season_index_batch sib                                                   ");
            sql.append("         ON sib.site_id =:siteId                                                     ");
            sql.append("        AND sib.facility_id  = :facilityId                                           ");
            sql.append("        AND sib.product_category_id = mpc.product_category_id                        ");
            sql.append("  LEFT JOIN (SELECT index_month01, index_month02, index_month03, index_month04       ");
            sql.append("                  , index_month05, index_month06, index_month07, index_month08       ");
            sql.append("                  , index_month09, index_month10, index_month11, index_month12       ");
            sql.append("                  , product_category_id                                              ");
            sql.append("               FROM season_index_manual                                              ");
            sql.append("              WHERE site_id = :siteId                                                ");
            sql.append("                AND facility_id = :facilityId                                        ");
            sql.append("                AND manual_flag = :yes                                               ");
            sql.append("            ) AS sim ON sib.n_index IS NULL                                          ");
            sql.append("        AND sim.product_category_id = mpc.product_category_id                        ");
            sql.append("      WHERE 1 = 1                                                                    ");

            if(model.getPartsId()!=null) {
                sql.append("      AND mp.product_id = :productId                                             ");
                params.put("productId",model.getPartsId());
            }

            if (model.getProductCategory() != null && !model.getProductCategory().isEmpty()) {
                if (model.getProductCategory().size() == 1) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                } else if(model.getProductCategory().size() == 2) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    sql.append(" AND split_part(mp.all_parent_id, '|', 2) = :middleGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                    params.put("middleGroupId", model.getProductCategory().get(1));
                }
            }
            sql.append("GROUP BY mp.product_cd, mp.sales_description, mp.product_id, mp.all_path, mp.all_nm, pai.abc_type, demandSource   ");
            sql.append(", df.demand_qty, rg.reorder_point, rg.reorder_qty, rg.rop_roq_manual_flag, sib.n_index, sim.index_month01         ");
            sql.append(", sim.index_month02, sim.index_month03, sim.index_month04, sim.index_month05, sim.index_month06, sim.index_month07");
            sql.append(", sim.index_month08, sim.index_month09, sim.index_month10, sim.index_month11, sim.index_month12                   ");

            params.put("current", PJConstants.DemandSource.CURRENTDEMAND);
        }
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource()) && !PJConstants.DemandSource.ORIGINALDEMAND.equals(model.getDemandSource())) {
            sql.append("    UNION ALL                                                                                                        ");
        }
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource())) {
            sql.append("     SELECT mp.product_cd                                                                        AS partsCd          ");
            sql.append("          , mp.sales_description                                                                 AS partsNm          ");
            sql.append("          , mp.product_id                                                                        AS partsId          ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1)                                                      AS largeGroupCd     ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1) || ' ' || SPLIT_PART(mp.all_nm, '|', 1)              AS largeGroupAbbr   ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2)                                                      AS middleGroupCd    ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2) || ' ' || SPLIT_PART(mp.all_nm, '|', 2)              AS middleGroupAbbr  ");
            sql.append("          , pai.abc_type                                                                         AS costUsage        ");
            sql.append("          , :original                                                                            AS demandSource     ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jOnePartsTotal  THEN prm.string_value ELSE NULL END)  AS jOne             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jTwoPartsTotal  THEN prm.string_value ELSE NULL END)  AS jTwo             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :partsTrendIndex THEN prm.string_value ELSE NULL END ) AS trendIndex       ");
            sql.append("          , df.demand_qty                                                                        AS demandForecast   ");
            sql.append("          , rg.reorder_point                                                                     AS rop              ");
            sql.append("          , rg.reorder_qty                                                                       AS roq              ");
            sql.append("          , rg.rop_roq_manual_flag                                                               AS manualSign       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :boQty  THEN pss.quantity ELSE 0 END)       AS boQty       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onHandQty  THEN pss.quantity ELSE 0 END)   AS onHandQty   ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in(:onHandQty,:allocatiedQty,:onpickingQty,:ontransferInQty,:onShipping,:onCanvassInQty,:onServiceQty,:onfrozenQty,:onBorrowingQty)         ");
            sql.append("                           THEN pss.quantity ELSE 0 END) AS totalStock                                               ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in (:onHandQty,:ontransferInQty,:onreceivingQty,:eoOnpurchaseQty,:hoonPurchaseQty,:roOnPurchaseQty)             ");
            sql.append("                              THEN pss.quantity ELSE 0 END) AS futureStock                                           ");
            sql.append("          , COALESCE(sib.n_index,                                                                                    ");
            sql.append("                       CASE WHEN to_char(current_date, 'MM') = '01' THEN sim.index_month01                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '02' THEN sim.index_month02                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '03' THEN sim.index_month03                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '04' THEN sim.index_month04                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '05' THEN sim.index_month05                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '06' THEN sim.index_month06                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '07' THEN sim.index_month07                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '08' THEN sim.index_month08                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '09' THEN sim.index_month09                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '10' THEN sim.index_month10                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '11' THEN sim.index_month11                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '12' THEN sim.index_month12                           ");
            sql.append("                        ELSE 0 END, 0 ) AS seasonIndex                                                               ");
            sql.append("       FROM mst_product mp                                                           ");
            sql.append("  LEFT JOIN mst_product_category mpc                                                 ");
            sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 1)                        ");
            sql.append("        AND mpc.site_id  = mp.site_id                                                ");
            sql.append("  LEFT JOIN product_abc_info pai                                                     ");
            sql.append("         ON pai.site_id = :siteId                                                    ");
            sql.append("        AND pai.facility_id = :facilityId                                            ");
            sql.append("        AND pai.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN parts_ropq_monthly prm                                                   ");
            sql.append("         ON prm.site_id = :siteId                                                    ");
            sql.append("        AND prm.facility_id = :facilityId                                            ");
            sql.append("        AND prm.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN demand_forecast df                                                       ");
            sql.append("         ON df.site_id = :siteId                                                     ");
            sql.append("        AND df.facility_id  =:facilityId                                             ");
            sql.append("        AND df.to_product_id  = mp.product_id                                        ");
            sql.append("        AND df.target_month = TO_CHAR(CURRENT_DATE, :yearAndMonth)                   ");
            sql.append("  LEFT JOIN reorder_guideline rg                                                     ");
            sql.append("         ON rg.site_id = :siteId                                                     ");
            sql.append("        AND rg.facility_id  =:facilityId                                             ");
            sql.append("        AND rg.product_id  = mp.product_id                                           ");
            sql.append("  LEFT JOIN product_stock_status pss                                                 ");
            sql.append("         ON pss.site_id = :siteId                                                    ");
            sql.append("        AND pss.facility_id  =:facilityId                                            ");
            sql.append("        AND pss.product_id  = mp.product_id                                          ");
            sql.append("  LEFT JOIN season_index_batch sib                                                   ");
            sql.append("         ON sib.site_id =:siteId                                                     ");
            sql.append("        AND sib.facility_id  = :facilityId                                           ");
            sql.append("        AND sib.product_category_id = mpc.product_category_id                        ");
            sql.append("  LEFT JOIN (SELECT index_month01, index_month02, index_month03, index_month04       ");
            sql.append("                  , index_month05, index_month06, index_month07, index_month08       ");
            sql.append("                  , index_month09, index_month10, index_month11, index_month12       ");
            sql.append("                  , product_category_id                                              ");
            sql.append("               FROM season_index_manual                                              ");
            sql.append("              WHERE site_id = :siteId                                                ");
            sql.append("                AND facility_id = :facilityId                                        ");
            sql.append("                AND manual_flag = :yes                                               ");
            sql.append("            ) AS sim ON sib.n_index IS NULL                                          ");
            sql.append("        AND sim.product_category_id = mpc.product_category_id                        ");
            sql.append("      WHERE 1 = 1                                                                    ");

            if(model.getPartsId()!=null) {
                sql.append("      AND mp.product_id = :productId                                             ");
                params.put("productId",model.getPartsId());
            }

            if (model.getProductCategory() != null && !model.getProductCategory().isEmpty()) {
                if (model.getProductCategory().size() == 1) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                } else if(model.getProductCategory().size() == 2) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    sql.append(" AND split_part(mp.all_parent_id, '|', 2) = :middleGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                    params.put("middleGroupId", model.getProductCategory().get(1));
                }
            }
            sql.append("GROUP BY mp.product_cd, mp.sales_description, mp.product_id, mp.all_path, mp.all_nm, pai.abc_type, demandSource   ");
            sql.append(", df.demand_qty, rg.reorder_point, rg.reorder_qty, rg.rop_roq_manual_flag, sib.n_index, sim.index_month01         ");
            sql.append(", sim.index_month02, sim.index_month03, sim.index_month04, sim.index_month05, sim.index_month06, sim.index_month07");
            sql.append(", sim.index_month08, sim.index_month09, sim.index_month10, sim.index_month11, sim.index_month12                   ");

            params.put("original", PJConstants.DemandSource.ORIGINALDEMAND);
        }
        params.put("jOnePartsTotal", PJConstants.RopRoqParameter.KEY_PARTSJ1TOTAL);
        params.put("jTwoPartsTotal", PJConstants.RopRoqParameter.KEY_PARTSJ2TOTAL);
        params.put("partsTrendIndex", PJConstants.RopRoqParameter.KEY_PARTSTRENDINDEX);
        params.put("boQty", PJConstants.SpStockStatus.BO_QTY.getCodeDbid());
        params.put("onHandQty", PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("allocatiedQty", PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        params.put("onpickingQty", PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid());
        params.put("ontransferInQty", PJConstants.SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        params.put("onShipping", PJConstants.SpStockStatus.ONSHIPPING.getCodeDbid());
        params.put("onCanvassInQty", PJConstants.SpStockStatus.ONCANVASSING_QTY.getCodeDbid());
        params.put("onServiceQty", PJConstants.SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        params.put("onfrozenQty", PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("onBorrowingQty", PJConstants.SpStockStatus.ONBORROWING_QTY.getCodeDbid());
        params.put("onreceivingQty", PJConstants.SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        params.put("eoOnpurchaseQty", PJConstants.SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        params.put("hoonPurchaseQty", PJConstants.SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        params.put("roOnPurchaseQty", PJConstants.SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        params.put("siteId", siteId);
        params.put("facilityId", model.getPointId());
        params.put("yes", CommonConstants.CHAR_Y);
        params.put("yearAndMonth", CommonConstants.DB_DATE_FORMAT_YM);

        String countSql = "SELECT COUNT(*) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);;
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, CMQ050801BO.class, pageable), pageable, count);
    }

    @Override
    public List<CMQ050801BO> findPartsSummaryExportList(CMQ050801Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        if(!PJConstants.DemandSource.ORIGINALDEMAND.equals(model.getDemandSource())) {
            sql.append("     SELECT mp.product_cd                                                                        AS partsCd          ");
            sql.append("          , mp.sales_description                                                                 AS partsNm          ");
            sql.append("          , mp.product_id                                                                        AS partsId          ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1)                                                      AS largeGroupCd     ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1) || ' ' || SPLIT_PART(mp.all_nm, '|', 1)              AS largeGroupAbbr   ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2)                                                      AS middleGroupCd    ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2) || ' ' || SPLIT_PART(mp.all_nm, '|', 2)              AS middleGroupAbbr  ");
            sql.append("          , pai.abc_type                                                                         AS costUsage        ");
            sql.append("          , :current                                                                             AS demandSource     ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jOnePartsTotal  THEN prm.string_value ELSE NULL END)  AS jOne             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jTwoPartsTotal  THEN prm.string_value ELSE NULL END)  AS jTwo             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :partsTrendIndex THEN prm.string_value ELSE NULL END ) AS trendIndex       ");
            sql.append("          , df.demand_qty                                                                        AS demandForecast   ");
            sql.append("          , rg.reorder_point                                                                     AS rop              ");
            sql.append("          , rg.reorder_qty                                                                       AS roq              ");
            sql.append("          , rg.rop_roq_manual_flag                                                               AS manualSign       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :boQty  THEN pss.quantity ELSE 0 END)       AS boQty       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onHandQty  THEN pss.quantity ELSE 0 END)   AS onHandQty   ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in(:onHandQty,:allocatiedQty,:onpickingQty,:ontransferInQty,:onShipping,:onCanvassInQty,:onServiceQty,:onfrozenQty,:onBorrowingQty)         ");
            sql.append("                           THEN pss.quantity ELSE 0 END) AS totalStock                                               ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in (:onHandQty,:ontransferInQty,:onreceivingQty,:eoOnpurchaseQty,:hoonPurchaseQty,:roOnPurchaseQty)             ");
            sql.append("                              THEN pss.quantity ELSE 0 END) AS futureStock                                           ");
            sql.append("          , COALESCE(sib.n_index,                                                                                    ");
            sql.append("                       CASE WHEN to_char(current_date, 'MM') = '01' THEN sim.index_month01                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '02' THEN sim.index_month02                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '03' THEN sim.index_month03                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '04' THEN sim.index_month04                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '05' THEN sim.index_month05                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '06' THEN sim.index_month06                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '07' THEN sim.index_month07                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '08' THEN sim.index_month08                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '09' THEN sim.index_month09                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '10' THEN sim.index_month10                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '11' THEN sim.index_month11                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '12' THEN sim.index_month12                           ");
            sql.append("                        ELSE 0 END, 0 ) AS seasonIndex                                                               ");
            sql.append("       FROM mst_product mp                                                           ");
            sql.append("  LEFT JOIN mst_product_category mpc                                                 ");
            sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 1)                        ");
            sql.append("        AND mpc.site_id  = mp.site_id                                                ");
            sql.append("  LEFT JOIN product_abc_info pai                                                     ");
            sql.append("         ON pai.site_id = :siteId                                                    ");
            sql.append("        AND pai.facility_id = :facilityId                                            ");
            sql.append("        AND pai.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN parts_ropq_monthly prm                                                   ");
            sql.append("         ON prm.site_id = :siteId                                                    ");
            sql.append("        AND prm.facility_id = :facilityId                                            ");
            sql.append("        AND prm.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN demand_forecast df                                                       ");
            sql.append("         ON df.site_id = :siteId                                                     ");
            sql.append("        AND df.facility_id  =:facilityId                                             ");
            sql.append("        AND df.to_product_id  = mp.product_id                                        ");
            sql.append("        AND df.target_month = TO_CHAR(CURRENT_DATE, :yearAndMonth)                   ");
            sql.append("  LEFT JOIN reorder_guideline rg                                                     ");
            sql.append("         ON rg.site_id = :siteId                                                     ");
            sql.append("        AND rg.facility_id  =:facilityId                                             ");
            sql.append("        AND rg.product_id  = mp.product_id                                           ");
            sql.append("  LEFT JOIN product_stock_status pss                                                 ");
            sql.append("         ON pss.site_id = :siteId                                                    ");
            sql.append("        AND pss.facility_id  =:facilityId                                            ");
            sql.append("        AND pss.product_id  = mp.product_id                                          ");
            sql.append("  LEFT JOIN season_index_batch sib                                                   ");
            sql.append("         ON sib.site_id =:siteId                                                     ");
            sql.append("        AND sib.facility_id  = :facilityId                                           ");
            sql.append("        AND sib.product_category_id = mpc.product_category_id                        ");
            sql.append("  LEFT JOIN (SELECT index_month01, index_month02, index_month03, index_month04       ");
            sql.append("                  , index_month05, index_month06, index_month07, index_month08       ");
            sql.append("                  , index_month09, index_month10, index_month11, index_month12       ");
            sql.append("                  , product_category_id                                              ");
            sql.append("               FROM season_index_manual                                              ");
            sql.append("              WHERE site_id = :siteId                                                ");
            sql.append("                AND facility_id = :facilityId                                        ");
            sql.append("                AND manual_flag = :yes                                               ");
            sql.append("            ) AS sim ON sib.n_index IS NULL                                          ");
            sql.append("        AND sim.product_category_id = mpc.product_category_id                        ");
            sql.append("      WHERE 1 = 1                                                                    ");

            if(model.getPartsId()!=null) {
                sql.append("      AND mp.product_id = :productId                                             ");
                params.put("productId",model.getPartsId());
            }

            if (model.getProductCategory() != null && !model.getProductCategory().isEmpty()) {
                if (model.getProductCategory().size() == 1) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                } else if(model.getProductCategory().size() == 2) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    sql.append(" AND split_part(mp.all_parent_id, '|', 2) = :middleGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                    params.put("middleGroupId", model.getProductCategory().get(1));
                }
            }
            sql.append("GROUP BY mp.product_cd, mp.sales_description, mp.product_id, mp.all_path, mp.all_nm, pai.abc_type, demandSource   ");
            sql.append(", df.demand_qty, rg.reorder_point, rg.reorder_qty, rg.rop_roq_manual_flag, sib.n_index, sim.index_month01         ");
            sql.append(", sim.index_month02, sim.index_month03, sim.index_month04, sim.index_month05, sim.index_month06, sim.index_month07");
            sql.append(", sim.index_month08, sim.index_month09, sim.index_month10, sim.index_month11, sim.index_month12                   ");

            params.put("current", PJConstants.DemandSource.CURRENTDEMAND);
        }
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource()) && !PJConstants.DemandSource.ORIGINALDEMAND.equals(model.getDemandSource())) {
            sql.append("    UNION ALL                                                                                                     ");
        }
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource())) {
            sql.append("     SELECT mp.product_cd                                                                        AS partsCd          ");
            sql.append("          , mp.sales_description                                                                 AS partsNm          ");
            sql.append("          , mp.product_id                                                                        AS partsId          ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1)                                                      AS largeGroupCd     ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 1) || ' ' || SPLIT_PART(mp.all_nm, '|', 1)              AS largeGroupAbbr   ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2)                                                      AS middleGroupCd    ");
            sql.append("          , SPLIT_PART(mp.all_path, '|', 2) || ' ' || SPLIT_PART(mp.all_nm, '|', 2)              AS middleGroupAbbr  ");
            sql.append("          , pai.abc_type                                                                         AS costUsage        ");
            sql.append("          , :original                                                                            AS demandSource     ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jOnePartsTotal  THEN prm.string_value ELSE NULL END)  AS jOne             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :jTwoPartsTotal  THEN prm.string_value ELSE NULL END)  AS jTwo             ");
            sql.append("          , MAX(CASE WHEN prm.ropq_type = :partsTrendIndex THEN prm.string_value ELSE NULL END ) AS trendIndex       ");
            sql.append("          , df.demand_qty                                                                        AS demandForecast   ");
            sql.append("          , rg.reorder_point                                                                     AS rop              ");
            sql.append("          , rg.reorder_qty                                                                       AS roq              ");
            sql.append("          , rg.rop_roq_manual_flag                                                               AS manualSign       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :boQty  THEN pss.quantity ELSE 0 END)       AS boQty       ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type = :onHandQty  THEN pss.quantity ELSE 0 END)   AS onHandQty   ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in(:onHandQty,:allocatiedQty,:onpickingQty,:ontransferInQty,:onShipping,:onCanvassInQty,:onServiceQty,:onfrozenQty,:onBorrowingQty)         ");
            sql.append("                           THEN pss.quantity ELSE 0 END) AS totalStock                                               ");
            sql.append("          , SUM(CASE WHEN pss.product_stock_status_type in (:onHandQty,:ontransferInQty,:onreceivingQty,:eoOnpurchaseQty,:hoonPurchaseQty,:roOnPurchaseQty)             ");
            sql.append("                              THEN pss.quantity ELSE 0 END) AS futureStock                                           ");
            sql.append("          , COALESCE(sib.n_index,                                                                                    ");
            sql.append("                       CASE WHEN to_char(current_date, 'MM') = '01' THEN sim.index_month01                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '02' THEN sim.index_month02                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '03' THEN sim.index_month03                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '04' THEN sim.index_month04                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '05' THEN sim.index_month05                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '06' THEN sim.index_month06                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '07' THEN sim.index_month07                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '08' THEN sim.index_month08                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '09' THEN sim.index_month09                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '10' THEN sim.index_month10                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '11' THEN sim.index_month11                           ");
            sql.append("                            WHEN to_char(current_date, 'MM') = '12' THEN sim.index_month12                           ");
            sql.append("                        ELSE 0 END, 0 ) AS seasonIndex                                                               ");
            sql.append("       FROM mst_product mp                                                           ");
            sql.append("  LEFT JOIN mst_product_category mpc                                                 ");
            sql.append("         ON mpc.category_cd = SPLIT_PART(mp.all_path, '|', 1)                        ");
            sql.append("        AND mpc.site_id  = mp.site_id                                                ");
            sql.append("  LEFT JOIN product_abc_info pai                                                     ");
            sql.append("         ON pai.site_id = :siteId                                                    ");
            sql.append("        AND pai.facility_id = :facilityId                                            ");
            sql.append("        AND pai.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN parts_ropq_monthly prm                                                   ");
            sql.append("         ON prm.site_id = :siteId                                                    ");
            sql.append("        AND prm.facility_id = :facilityId                                            ");
            sql.append("        AND prm.product_id = mp.product_id                                           ");
            sql.append("  LEFT JOIN demand_forecast df                                                       ");
            sql.append("         ON df.site_id = :siteId                                                     ");
            sql.append("        AND df.facility_id  =:facilityId                                             ");
            sql.append("        AND df.to_product_id  = mp.product_id                                        ");
            sql.append("        AND df.target_month = TO_CHAR(CURRENT_DATE, :yearAndMonth)                   ");
            sql.append("  LEFT JOIN reorder_guideline rg                                                     ");
            sql.append("         ON rg.site_id = :siteId                                                     ");
            sql.append("        AND rg.facility_id  =:facilityId                                             ");
            sql.append("        AND rg.product_id  = mp.product_id                                           ");
            sql.append("  LEFT JOIN product_stock_status pss                                                 ");
            sql.append("         ON pss.site_id = :siteId                                                    ");
            sql.append("        AND pss.facility_id  =:facilityId                                            ");
            sql.append("        AND pss.product_id  = mp.product_id                                          ");
            sql.append("  LEFT JOIN season_index_batch sib                                                   ");
            sql.append("         ON sib.site_id =:siteId                                                     ");
            sql.append("        AND sib.facility_id  = :facilityId                                           ");
            sql.append("        AND sib.product_category_id = mpc.product_category_id                        ");
            sql.append("  LEFT JOIN (SELECT index_month01, index_month02, index_month03, index_month04       ");
            sql.append("                  , index_month05, index_month06, index_month07, index_month08       ");
            sql.append("                  , index_month09, index_month10, index_month11, index_month12       ");
            sql.append("                  , product_category_id                                              ");
            sql.append("               FROM season_index_manual                                              ");
            sql.append("              WHERE site_id = :siteId                                                ");
            sql.append("                AND facility_id = :facilityId                                        ");
            sql.append("                AND manual_flag = :yes                                               ");
            sql.append("            ) AS sim ON sib.n_index IS NULL                                          ");
            sql.append("        AND sim.product_category_id = mpc.product_category_id                        ");
            sql.append("      WHERE 1 = 1                                                                    ");

            if(model.getPartsId()!=null) {
                sql.append("      AND mp.product_id = :productId                                             ");
                params.put("productId",model.getPartsId());
            }

            if (model.getProductCategory() != null && !model.getProductCategory().isEmpty()) {
                if (model.getProductCategory().size() == 1) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                } else if(model.getProductCategory().size() == 2) {
                    sql.append(" AND split_part(mp.all_parent_id, '|', 1) = :largeGroupId ");
                    sql.append(" AND split_part(mp.all_parent_id, '|', 2) = :middleGroupId ");
                    params.put("largeGroupId", model.getProductCategory().get(0));
                    params.put("middleGroupId", model.getProductCategory().get(1));
                }
            }
            sql.append("GROUP BY mp.product_cd, mp.sales_description, mp.product_id, mp.all_path, mp.all_nm, pai.abc_type, demandSource   ");
            sql.append(", df.demand_qty, rg.reorder_point, rg.reorder_qty, rg.rop_roq_manual_flag, sib.n_index, sim.index_month01         ");
            sql.append(", sim.index_month02, sim.index_month03, sim.index_month04, sim.index_month05, sim.index_month06, sim.index_month07");
            sql.append(", sim.index_month08, sim.index_month09, sim.index_month10, sim.index_month11, sim.index_month12                   ");

            params.put("original", PJConstants.DemandSource.ORIGINALDEMAND);
        }
        params.put("jOnePartsTotal", PJConstants.RopRoqParameter.KEY_PARTSJ1TOTAL);
        params.put("jTwoPartsTotal", PJConstants.RopRoqParameter.KEY_PARTSJ2TOTAL);
        params.put("partsTrendIndex", PJConstants.RopRoqParameter.KEY_PARTSTRENDINDEX);
        params.put("boQty", PJConstants.SpStockStatus.BO_QTY.getCodeDbid());
        params.put("onHandQty", PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("allocatiedQty", PJConstants.SpStockStatus.ALLOCATED_QTY.getCodeDbid());
        params.put("onpickingQty", PJConstants.SpStockStatus.ONPICKING_QTY.getCodeDbid());
        params.put("ontransferInQty", PJConstants.SpStockStatus.ONTRANSFER_IN_QTY.getCodeDbid());
        params.put("onShipping", PJConstants.SpStockStatus.ONSHIPPING.getCodeDbid());
        params.put("onCanvassInQty", PJConstants.SpStockStatus.ONCANVASSING_QTY.getCodeDbid());
        params.put("onServiceQty", PJConstants.SpStockStatus.ONSERVICE_QTY.getCodeDbid());
        params.put("onfrozenQty", PJConstants.SpStockStatus.ONFROZEN_QTY.getCodeDbid());
        params.put("onBorrowingQty", PJConstants.SpStockStatus.ONBORROWING_QTY.getCodeDbid());
        params.put("onreceivingQty", PJConstants.SpStockStatus.ONRECEIVING_QTY.getCodeDbid());
        params.put("eoOnpurchaseQty", PJConstants.SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        params.put("hoonPurchaseQty", PJConstants.SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        params.put("roOnPurchaseQty", PJConstants.SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        params.put("siteId", siteId);
        params.put("facilityId", model.getPointId());
        params.put("yes", CommonConstants.CHAR_Y);
        params.put("yearAndMonth", CommonConstants.DB_DATE_FORMAT_YM);

        return super.queryForList(sql.toString(), params, CMQ050801BO.class);
    }

    @Override
    public List<PartsInfoBO> findPartsInfoList(List<String> partsNoList, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH p_list AS (                                             ");
        sql.append("    SELECT mb.brand_nm                  AS brand             ");
        sql.append("         , mp.product_id                AS productId         ");
        sql.append("         , mp.product_cd                AS productCd         ");
        sql.append("         , mp.sales_description         AS productNm         ");
        sql.append("         , mp.sal_lot_size              AS salLotSize        ");
        sql.append("         , mp.min_pur_qty               AS minPurQty         ");
        sql.append("         , mp.std_retail_price          AS stdRetailPrice    ");
        sql.append("         , mp.battery_flag              AS batteryFlag       ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup        ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup       ");
        sql.append("         , mp.product_retrieve          AS productRetrieve   ");
        sql.append("         , mp.brand_id                  AS brandId           ");
        sql.append("         , mp.all_parent_id             AS allParentId       ");
        sql.append("         , mp.sales_status_type         AS partStatus        ");
        sql.append("         , mp.product_category_id                            ");
        sql.append("         , mp.pur_lot_size              AS purLotSize        ");
        sql.append("      FROM mst_product mp                                    ");
        sql.append("         , mst_brand mb                                      ");
        sql.append("     WHERE mp.site_id = :siteId1                             ");
        sql.append("       AND mp.product_classification = :pc                   ");
        sql.append("       AND mb.site_id = mp.site_id                           ");
        sql.append("       AND mb.brand_id = mp.brand_id                         ");
        sql.append(" UNION ALL                                                   ");
        sql.append("    SELECT mb.brand_nm                  AS brand             ");
        sql.append("         , mp.product_id                AS productId         ");
        sql.append("         , mp.product_cd                AS productCd         ");
        sql.append("         , mp.sales_description         AS productNm         ");
        sql.append("         , mp.sal_lot_size              AS salLotSize        ");
        sql.append("         , mp.min_pur_qty               AS minPurQty         ");
        sql.append("         , mp.std_retail_price          AS stdRetailPrice    ");
        sql.append("         , mp.battery_flag              AS batteryFlag       ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup        ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup       ");
        sql.append("         , mp.product_retrieve          AS productRetrieve   ");
        sql.append("         , mp.brand_id                  AS brandId           ");
        sql.append("         , mp.all_parent_id             AS allParentId       ");
        sql.append("         , mp.sales_status_type         AS partStatus        ");
        sql.append("         , mp.product_category_id                            ");
        sql.append("         , mp.pur_lot_size              AS purLotSize        ");
        sql.append("    FROM mst_product mp                                      ");
        sql.append("       , mst_brand mb                                        ");
        sql.append("   WHERE mp.site_id = :siteId2                               ");
        sql.append("     AND mp.product_classification = :pc                     ");
        sql.append("     AND mb.brand_id = mp.brand_id                           ");
        sql.append("    )                                                        ");
        sql.append("    SELECT pl.brand                       AS brand           ");
        sql.append("         , pl.productId                   AS partsId         ");
        sql.append("         , pl.productCd                   AS partsNo         ");
        sql.append("         , pl.productNm                   AS partsNm         ");
        sql.append("         , CONCAT(pl.productCd, ' ', pl.productNm) AS desc   ");
        sql.append("         , pl.salLotSize                  AS salLotSize      ");
        sql.append("         , pl.stdRetailPrice              AS stdRetailPrice  ");
        sql.append("         , pl.batteryFlag                 AS batteryFlag     ");
        sql.append("         , pl.largeGroup                  AS largeGroup      ");
        sql.append("         , pl.middlegroup                 AS middleGroup     ");
        sql.append("         , pl.productRetrieve             AS productRetrieve ");
        sql.append("         , pl.brandId                     AS brandId         ");
        sql.append("         , pl.allParentId                 AS allParentId     ");
        sql.append("         , pl.minPurQty                   AS minPurQty       ");
        sql.append("         , COALESCE(pc.cost, pc2.cost, 0) AS price           ");
        sql.append("         , pl.purLotSize                  AS purLotSize      ");
        sql.append("      FROM p_list pl                                         ");
        sql.append(" LEFT JOIN product_cost pc                                   ");
        sql.append("        ON pc.site_id = :siteId2                             ");
        sql.append("       AND pc.product_id = pl.productId                      ");
        sql.append("       AND pc.cost_type = :costType                          ");
        sql.append(" LEFT JOIN product_cost pc2                                  ");
        sql.append("        ON pc2.site_id = :siteId2                            ");
        sql.append("       AND pc2.product_id = pl.productId                     ");
        sql.append("       AND pc2.cost_type = :costType2                        ");
        sql.append("     WHERE pl.productCd IN (:partsNoList)                    ");

        params.put("siteId1", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("siteId2", siteId);
        params.put("pc", ProductClsType.PART.getCodeDbid());
        params.put("costType", CostType.RECEIVE_COST);
        params.put("costType2", CostType.AVERAGE_COST);
        params.put("partsNoList", partsNoList);

        return super.queryForList(sql.toString(), params, PartsInfoBO.class);
    }

    @Override
    public List<PartsInfoBO> findYamahaPartsInfoList(List<String> partsNoList,String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("WITH p_list AS (                                                      ");
        sql.append("    SELECT mb.brand_nm                  AS brand                      ");
        sql.append("         , mp.product_id                AS productId                  ");
        sql.append("         , mp.product_cd                AS productCd                  ");
        sql.append("         , mp.sales_description         AS productNm                  ");
        sql.append("         , mp.sal_lot_size              AS salLotSize                 ");
        sql.append("         , mp.min_pur_qty               AS minPurQty                  ");
        sql.append("         , mp.std_retail_price          AS stdRetailPrice             ");
        sql.append("         , mp.battery_flag              AS batteryFlag                ");
        sql.append("         , split_part(mp.all_nm ,'|',1) AS largeGroup                 ");
        sql.append("         , split_part(mp.all_nm ,'|',2) AS middleGroup                ");
        sql.append("         , mp.product_retrieve          AS productRetrieve            ");
        sql.append("         , mp.brand_id                  AS brandId                    ");
        sql.append("         , mp.all_parent_id             AS allParentId                ");
        sql.append("         , mp.sales_status_type         AS partStatus                 ");
        sql.append("         , mp.product_category_id       AS productCategoryId          ");
        sql.append("         , mp.pur_lot_size              AS purLotSize                 ");
        sql.append("      FROM mst_product mp                                             ");
        sql.append("         , mst_brand mb                                               ");
        sql.append("     WHERE mp.site_id = :cmmSiteId                                    ");
        sql.append("       AND mp.product_classification = :pc                            ");
        sql.append("       AND mb.site_id = mp.site_id                                    ");
        sql.append("       AND mb.brand_id = mp.brand_id                                  ");
        sql.append("    )                                                                 ");
        sql.append("    SELECT pl.brand                                AS brand           ");
        sql.append("         , pl.productId                            AS partsId         ");
        sql.append("         , pl.productCd                            AS partsNo         ");
        sql.append("         , pl.productNm                            AS partsNm         ");
        sql.append("         , CONCAT(pl.productCd, ' ', pl.productNm) AS desc            ");
        sql.append("         , pl.salLotSize                           AS salLotSize      ");
        sql.append("         , pl.stdRetailPrice                       AS stdRetailPrice  ");
        sql.append("         , pl.batteryFlag                          AS batteryFlag     ");
        sql.append("         , pl.largeGroup                           AS largeGroup      ");
        sql.append("         , pl.middlegroup                          AS middleGroup     ");
        sql.append("         , pl.productRetrieve                      AS productRetrieve ");
        sql.append("         , pl.brandId                              AS brandId         ");
        sql.append("         , pl.allParentId                          AS allParentId     ");
        sql.append("         , pl.minPurQty                            AS minPurQty       ");
        sql.append("         , COALESCE(pc.cost, pc2.cost, 0)          AS price           ");
        sql.append("         , pl.purLotSize                           AS purLotSize      ");
        sql.append("      FROM p_list pl                                                  ");
        sql.append(" LEFT JOIN product_cost pc                                            ");
        sql.append("        ON pc.site_id = :siteId                                       ");
        sql.append("       AND pc.product_id = pl.productId                               ");
        sql.append("       AND pc.cost_type = :costType                                   ");
        sql.append(" LEFT JOIN product_cost pc2                                           ");
        sql.append("        ON pc2.site_id = :siteId                                      ");
        sql.append("       AND pc2.product_id = pl.productId                              ");
        sql.append("       AND pc2.cost_type = :costType2                                 ");
        sql.append("     WHERE pl.productCd IN (:partsNoList)                             ");

        params.put("cmmSiteId",CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("siteId", siteId);
        params.put("pc", ProductClsType.PART.getCodeDbid());
        params.put("costType", CostType.RECEIVE_COST);
        params.put("costType2", CostType.AVERAGE_COST);
        params.put("partsNoList", partsNoList);

        return super.queryForList(sql.toString(), params, PartsInfoBO.class);
    }

    @Override
    public Long getModelCategoryIdByLvOneModelId(Long modelId) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT mp2.product_category_id           ");
        sql.append("      FROM mst_product mp                    ");
        sql.append(" LEFT JOIN mst_product mp2                   ");
        sql.append("        ON mp2.product_id = mp.to_product_id ");
        sql.append(" LEFT JOIN mst_product_category mpc          ");
        sql.append("        ON mpc.product_category_id = mp2.product_category_id ");
        sql.append("     WHERE mp.product_id = :modelId             ");
        sql.append("       AND mpc.category_cd IN (:serviceModelTypeList)      ");

        params.put("modelId",modelId);
        params.put("serviceModelTypeList", Arrays.asList(new String[] {CategoryCd.AT, CategoryCd.MP, CategoryCd.BIGBIKE, CategoryCd.EV}));
        return super.queryForSingle(sql.toString(), params, Long.class);
    }

    @Override
    public List<PartsInfoBO> listProductInfoForAllocate(Set<Long> productIds) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append(" SELECT product_id         as partsId        ");
        sql.append("      , sales_status_type  as salesStatusType  ");
        sql.append("      , local_description  as partsNm        ");
        sql.append("      , product_cd         as partsNo        ");
        sql.append("      , std_retail_price   as price       ");
        sql.append("      , sal_lot_size       as salLotSize       ");
        sql.append("   FROM mst_product                                ");
        sql.append("  WHERE product_id IN (:productIds)            ");

        param.put("productIds",productIds);

        return super.queryForList(sql.toString(), param, PartsInfoBO.class);
    }

    @Override
    public Page<CMM040301BO> getModelInfoInquiry(CMM040301Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mp.product_cd                         AS modelCd         ");
        sql.append("      , mp.sales_description                  AS modelNm         ");
        sql.append("      , mp.product_id                         AS modelId         ");
        sql.append("      , mp.color_nm                           AS colorNm         ");
        sql.append("      , substring(mp.registration_date, 1, 4) AS modelYear       ");
        sql.append("      , substring(mp.product_cd, 1, 4)        AS registeredModel ");
        sql.append("      , mp.expired_date                       AS expiredDate     ");
        sql.append("   FROM mst_product mp                                           ");
        sql.append("  WHERE mp.product_classification  =:GOODS                       ");
        sql.append("    AND mp.product_level = :ONE                                  ");

        if (!StringUtils.isEmpty(form.getModel())) {
            sql.append("    AND mp.product_retrieve LIKE :model                      ");
            params.put("model", "%" + form.getModel() + "%");
        }

        if (!StringUtils.isEmpty(form.getModelYear())) {
            sql.append("    AND substring(mp.registration_date, 1, 4) = :modelYear   ");
            params.put("modelYear", form.getModelYear());
        }

        sql.append("  ORDER BY mp.product_cd asc                                     ");

        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());
        params.put("ONE", CommonConstants.INTEGER_ONE);

        String countSql = "SELECT COUNT(*) FROM ( " + sql.toString() + ") AS subquery; ";
        Integer count = super.queryForSingle(countSql, params, Integer.class);;
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, CMM040301BO.class, pageable), pageable, count);
    }

    @Override
    public List<CMM090101BO> findModelPriceList(CMM090101Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT mp.product_cd        AS modelCode,       ");
        sql.append("        mp.product_id        AS productId,       ");
        sql.append("        mp.sales_description AS modelName,       ");
        sql.append("        mp.registration_date AS registrationDate,");
        sql.append("        mp.expired_date      AS expiredDate,     ");
        sql.append("        mp.update_count      AS updateCount,     ");

        if(StringUtils.equals(form.getPriceType(), PJConstants.PriceCategory.GOODSRETAILPRICE.getCodeDbid())){
            sql.append("        mp.std_retail_price  AS price     ");
        }else if(StringUtils.equals(form.getPriceType(), PJConstants.PriceCategory.GOODSEMPLOYEEPRICE.getCodeDbid())){
            sql.append("        mp.std_ws_price      AS price     ");
        }

        sql.append("   FROM mst_product mp                           ");
        sql.append("  WHERE mp.product_classification = :GOODS       ");

        if (form.getModelId() != null) {
            sql.append("    AND mp.product_id =:modelId               ");
            params.put("modelId", form.getModelId());
        }

        sql.append("  ORDER BY mp.product_cd asc                      ");

        params.put("GOODS", ProductClsType.GOODS.getCodeDbid());

        return super.queryForList(sql.toString(), params, CMM090101BO.class);
    }

    @Override
    public List<ProductBO> findMstProductInfo(Set<String> productCds, String productCls) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT product_cd           AS productCd               ");
        sql.append("         , product_id           AS productId               ");
        sql.append("      FROM mst_product                                     ");
        sql.append("     WHERE site_id = :siteId                               ");
        sql.append("     AND product_classification = :productCls              ");

//        if(StringUtils.equals(productCls, ProductClsType.GOODS.getCodeDbid())) {
//            sql.append("   AND product_cd like :productCd                      ");
//            params.put("productCd", productCd + CommonConstants.CHAR_PERCENT);
//        } else if(StringUtils.equals(productCls, ProductClsType.PART.getCodeDbid())) {
//            if (productCd.length() == 5) {
//                sql.append("   AND product_cd like :productCd                  ");
//                params.put("productCd",CommonConstants.CHAR_PERCENT + productCd + CommonConstants.CHAR_PERCENT);
//            } else {
//                sql.append("   AND product_cd =:productCd                      ");
//                params.put("productCd",productCd);
//            }
//        }
        String queryLike = queryPlaceHolders(productCds);
        sql.append("   AND product_cd like ANY(ARRAY[%" + queryLike + "%] ) " );


        params.put("siteId", CommonConstants.CHAR_DEFAULT_SITE_ID);
        params.put("productCls", productCls);

        return super.queryForList(sql.toString(), params, ProductBO.class);
    }

    /**
     * @param productCds
     */
    private String queryPlaceHolders(Set<String> productCds) {

        StringBuilder codes = new StringBuilder();
        for (int i = 0; i < productCds.size(); i++) {
            if (i > 0) {
                codes.append(",");
            }
            codes.append("?");
        }

        return codes.toString();
    }
}