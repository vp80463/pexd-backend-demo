package com.a1stream.domain.custom.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.parts.SPQ030201BO;
import com.a1stream.domain.bo.parts.SPQ030401BO;
import com.a1stream.domain.bo.unit.SDQ011401BO;
import com.a1stream.domain.custom.InventoryTransactionCustom;
import com.a1stream.domain.form.parts.SPQ030201Form;
import com.a1stream.domain.form.parts.SPQ030401Form;
import com.a1stream.domain.form.unit.SDQ011401Form;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class InventoryTransactionCustomImpl extends JpaNativeQuerySupportRepository implements InventoryTransactionCustom {

    @Override
    public List<SPQ030401BO> findPartsInOutHistoryList(SPQ030401Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT it.product_cd                                           AS partsNo               ");
        sql.append("         , it.product_nm                                           AS partsNm               ");
        sql.append("         , mf.facility_cd                                          AS pointCd               ");
        sql.append("         , mf.facility_nm                                          AS pointNm               ");
        sql.append("         , it.physical_transaction_date                            AS transactionDate       ");
        sql.append("         , it.physical_transaction_time                            AS transactionTime       ");
        sql.append("         , it.inventory_transaction_type                           AS transactionTypeId     ");
        sql.append("         , it.inventory_transaction_nm                             AS transactionType       ");
        sql.append("         , mo_from.organization_nm                                 AS from                  ");
        sql.append("         , mo_to.organization_nm                                   AS to                    ");
        sql.append("         , CASE WHEN it.in_qty  IS NULL THEN 0 ELSE it.in_qty  END AS inQty                 ");
        sql.append("         , CASE WHEN it.out_qty IS NULL THEN 0 ELSE it.out_qty END AS outQty                ");
        sql.append("         , it.in_cost + it.out_cost                                AS cost                  ");
        sql.append("         , it.current_qty                                          AS afterQty              ");
        sql.append("         , it.current_average_cost                                 AS averageCost           ");
        sql.append("         , it.related_slip_no                                      AS transactionNo         ");
        sql.append("      FROM inventory_transaction it                                                         ");
        sql.append(" LEFT JOIN mst_organization mo_from                                                         ");
        sql.append("        ON mo_from.organization_id = it.from_organization_id                                ");
        sql.append(" LEFT JOIN mst_organization mo_to                                                           ");
        sql.append("        ON mo_to.organization_id = it.to_organization_id                                    ");
        sql.append(" LEFT JOIN mst_facility mf                                                                  ");
        sql.append("        ON mf.facility_id = it.target_facility_id                                           ");
        sql.append("     WHERE it.physical_transaction_date >= :fromDate                                        ");
        sql.append("       AND it.physical_transaction_date <= :toDate                                          ");
        sql.append("       AND it.target_facility_id = :pointId                                                 ");
        sql.append("       AND it.product_id = :partsId                                                         ");
        sql.append("       AND it.site_id = :siteId                                                             ");
        params.put("fromDate", model.getDateFrom());
        params.put("toDate", model.getDateTo());
        params.put("pointId", model.getPointId());
        params.put("partsId", model.getPartsId());
        params.put("siteId", siteId);

        sql.append("  ORDER BY it.physical_transaction_date, it.physical_transaction_time  ");

        return super.queryForList(sql.toString(), params, SPQ030401BO.class);
    }

    @Override
    public List<SPQ030201BO> findPartsAdjustmentHistoryList(SPQ030201Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        sql.append("    SELECT it.product_cd                                  AS partsNo                 ");
        sql.append("         , it.product_nm                                  AS partsNm                 ");
        sql.append("         , it.product_id                                  AS partsId                 ");
        sql.append("         , it.stock_adjustment_reason_nm                  AS adjustmentReason        ");
        sql.append("         , it.stock_adjustment_reason_type                AS adjustmentReasonTypeId  ");
        sql.append("         , it.physical_transaction_date                   AS adjustmentDate          ");
        sql.append("         , it.physical_transaction_time                   AS adjustmentTime          ");
        sql.append("         , it.location_cd                                 AS location                ");
        sql.append("         , it.location_id                                 AS locationId              ");
        sql.append("         , it.in_qty                                      AS inQty                   ");
        sql.append("         , it.in_cost                                     AS inCost                  ");
        sql.append("         , it.in_cost * it.in_qty                         AS inAmount                ");
        sql.append("         , it.out_qty                                     AS outQty                  ");
        sql.append("         , it.out_cost                                    AS outCost                 ");
        sql.append("         , it.out_cost * it.out_qty                       AS outAmount               ");
        sql.append("         , it.reporter_nm                                 AS pic                     ");
        sql.append("      FROM inventory_transaction it                                                  ");
        sql.append("     WHERE it.physical_transaction_date >= :fromDate                                 ");
        sql.append("       AND it.physical_transaction_date <= :toDate                                   ");
        sql.append("       AND it.target_facility_id = :pointId                                          ");
        sql.append("       AND it.site_id = :siteId                                                      ");
        sql.append("       AND it.inventory_transaction_type IN (:ADJUSTIN,:ADJUSTOUT)                   ");

        if (StringUtils.isNotBlank(model.getStockAdjustmentReason())) {
            sql.append("   AND it.stock_adjustment_reason_type = :adjustmentReasonType                   ");
            params.put("adjustmentReasonType", model.getStockAdjustmentReason());
        }

        if( !ObjectUtils.isEmpty(model.getPartsId())) {
            sql.append("       AND it.product_id = :partsId                                              ");
            params.put("partsId", model.getPartsId());
        }

        params.put("fromDate", model.getDateFrom());
        params.put("toDate", model.getDateTo());
        params.put("pointId", model.getPointId());
        params.put("siteId", siteId);
        params.put("ADJUSTIN", PJConstants.InventoryTransactionType.ADJUSTIN.getCodeDbid());
        params.put("ADJUSTOUT", PJConstants.InventoryTransactionType.ADJUSTOUT.getCodeDbid());

        sql.append("  ORDER BY it.product_cd, it.physical_transaction_date, it.physical_transaction_time  ");

        return super.queryForList(sql.toString(), params, SPQ030201BO.class);
    }

    @Override
    public Page<SDQ011401BO> getStockInOutHistoryList(SDQ011401Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("  SELECT mf.facility_id                            AS facilityId             ");
        selSql.append("       , mf.facility_cd                            AS pointCd                ");
        selSql.append("       , mf.facility_nm                            AS pointNm                ");
        selSql.append("       , mp.product_id                             AS modelId                ");
        selSql.append("       , mp.product_cd                             AS modelCd                ");
        selSql.append("       , mp.sales_description                      AS modelNm                ");
        selSql.append("       , it.related_slip_no                        AS transactionNo          ");
        selSql.append("       , it.related_slip_id                        AS relatedSlipId          ");
        selSql.append("       , it.inventory_transaction_type             AS transactionTypeCd      ");
        selSql.append("       , it.physical_transaction_date              AS transactionDate        ");
        selSql.append("       , it.from_facility_id                       AS fromFacilityId         ");
        selSql.append("       , it.to_facility_id                         AS toFacilityId           ");
        selSql.append("       , it.from_organization_id                   AS fromOrganizationId     ");
        selSql.append("       , it.to_organization_id                     AS toOrganizationId       ");
        selSql.append("       , it.in_qty                                 AS inQty                  ");
        selSql.append("       , it.out_qty                                AS outQty                 ");
        selSql.append("       , it.current_qty                            AS stockQty               ");
        sql.append("       FROM inventory_transaction it                                            ");
        sql.append(" INNER JOIN mst_product mp                                                      ");
        sql.append("         ON it.product_id = mp.product_id                                       ");

        if (!Nulls.isNull(form.getModelId())) {
            sql.append("    AND mp.product_id  = :modelId                                     ");
        }

        sql.append(" INNER JOIN mst_facility mf                                                     ");
        sql.append("         ON mf.facility_id = it.target_facility_id                              ");
        sql.append("      WHERE it.site_id = :siteId                                                ");
        sql.append("        AND it.product_classification = :S001GOODS                              ");
        sql.append("        AND it.physical_transaction_date >= :dateFrom                           ");
        sql.append("        AND it.physical_transaction_date <= :dateTo                             ");

        params.put("siteId", form.getSiteId());
        params.put("S001GOODS", ProductClsType.GOODS.getCodeDbid());
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        if (!Nulls.isNull(form.getPointId())) {
            sql.append("    AND it.target_facility_id = :pointId                                    ");
            params.put("pointId", form.getPointId());
        }

        if (!Nulls.isNull(form.getModelId())) {
            sql.append("    AND mp.product_id = :modelId                                         ");
            params.put("modelId", form.getModelId());
        }

        sql.append("  ORDER BY mf.facility_cd ASC, it.physical_transaction_date ASC                ");
        sql.append("         , it.physical_transaction_time ASC, it.related_slip_no ASC            ");

        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SDQ011401BO.class, pageable), pageable, count);
    }
}
