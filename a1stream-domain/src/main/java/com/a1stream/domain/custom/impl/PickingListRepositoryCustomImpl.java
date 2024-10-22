package com.a1stream.domain.custom.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.domain.bo.parts.SPQ020601BO;
import com.a1stream.domain.bo.parts.SPQ020601PrintBO;
import com.a1stream.domain.bo.parts.SPQ020602BO;
import com.a1stream.domain.custom.PickingListRepositoryCustom;
import com.a1stream.domain.form.parts.SPQ020601Form;
import com.a1stream.domain.form.parts.SPQ020602Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class PickingListRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements PickingListRepositoryCustom {

    @Override
    public Page<SPQ020601BO> getPickingInstructionList(SPQ020601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder selSql = new StringBuilder();
        StringBuilder sql = new StringBuilder();

        selSql.append("  SELECT pl.picking_list_id              AS pickingListId              ");
        selSql.append("       , pl.picking_list_no              AS pickingListNo              ");
        selSql.append("       , do1.delivery_order_no           AS duNo                       ");
        selSql.append("       , do1.delivery_order_id           AS deliveryOrderId            ");
        selSql.append("       , do1.inventory_transaction_type  AS transactionTypeCd          ");
        selSql.append("       , do1.delivery_order_date         AS instructionDate            ");
        selSql.append("       , do1.customer_cd                 AS customerCd                 ");
        selSql.append("       , do1.customer_nm                 AS customerNm                 ");
        selSql.append("       , COUNT(pl.picking_list_id)       AS pickingLines               ");
        selSql.append("       , do1.delivery_status             AS status                     ");
        sql.append("       FROM picking_list pl                                               ");
        sql.append("          , delivery_order do1                                            ");
        sql.append("          , picking_item pi2                                              ");
        sql.append("      WHERE do1.delivery_order_id = pi2.delivery_order_id                 ");
        sql.append("        AND pi2.picking_list_id = pl.picking_list_id                      ");
        sql.append("        AND do1.site_id = :siteId                                         ");
        sql.append("        AND pl.site_id = :siteId                                          ");
        sql.append("        AND do1.from_facility_id = :facilityId                            ");
        sql.append("        AND do1.delivery_order_date >= :dateFrom                          ");
        sql.append("        AND do1.delivery_order_date <= :dateTo                            ");

        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getPointId());
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        if (StringUtils.isNotBlank(form.getDuStatus())) {
            sql.append("  AND do1.delivery_status = :duStatus ");
            params.put("duStatus", form.getDuStatus());
        }
        if (StringUtils.isNotBlank(form.getDuNo())) {
            sql.append("  AND do1.delivery_order_no = :duNo ");
            params.put("duNo", form.getDuNo());
        }
        if (StringUtils.isNotBlank(form.getTransactionType())) {
            sql.append("  AND do1.inventory_transaction_type = :transactionType ");
            params.put("transactionType", form.getTransactionType());
        }
        if (StringUtils.equals(CommonConstants.CHAR_Y, form.getUnfinishedOnly())) {
            sql.append("  AND do1.delivery_status IN (:duStatusList) ");
            List<String> list = Arrays.asList(DeliveryStatus.CREATED,
                                              DeliveryStatus.ON_PICKING,
                                              DeliveryStatus.PACKING_COMPLETE,
                                              DeliveryStatus.ON_PACKING);
            params.put("duStatusList", list);
        }
        String countSql = "SELECT COUNT(*) FROM ( " + "SELECT 1 " + sql.toString() + ") AS subquery; ";

        sql.append(" GROUP BY pl.picking_list_id, pl.picking_list_no, do1.delivery_order_id,                                ");
        sql.append("          do1.inventory_transaction_type , do1.delivery_order_date, do1.customer_cd, do1.customer_nm    ");
        Pageable pageable = PageRequest.of(form.getCurrentPage() - 1, form.getPageSize());
        if (!Boolean.TRUE.equals(form.getPageFlg())) {
            pageable = Pageable.unpaged();
        }
        Integer count = super.queryForSingle(countSql, params, Integer.class);
        return new PageImpl<>(super.queryForPagingList(selSql.append(sql).toString(), params, SPQ020601BO.class, pageable), pageable, count);
    }

    @Override
    public List<SPQ020601BO> getPickingInstructionExportData(SPQ020601Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("  SELECT pl.picking_list_no       AS pickingListNo              ");
        sql.append("       , do1.delivery_order_no    AS duNo                       ");
        sql.append("       , pi2.location_cd          AS locationCd                 ");
        sql.append("       , pi2.product_cd           AS productCd                  ");
        sql.append("       , pi2.product_nm           AS productNm                  ");
        sql.append("       , pi2.qty                  AS pickingQty                 ");
        sql.append("    FROM picking_list pl                                        ");
        sql.append("       , delivery_order do1                                     ");
        sql.append("       , picking_item pi2                                       ");
        sql.append("   WHERE do1.delivery_order_id = pi2.delivery_order_id          ");
        sql.append("     AND pi2.picking_list_id = pl.picking_list_id               ");
        sql.append("     AND do1.site_id = :siteId                                  ");
        sql.append("     AND pl.site_id = :siteId                                   ");
        sql.append("     AND do1.from_facility_id = :facilityId                     ");
        sql.append("     AND do1.delivery_order_date >= :dateFrom                   ");
        sql.append("     AND do1.delivery_order_date <= :dateTo                     ");

        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getPointId());
        params.put("dateFrom", form.getDateFrom());
        params.put("dateTo", form.getDateTo());

        if (StringUtils.isNotBlank(form.getDuStatus())) {
            sql.append("  AND do1.delivery_status = :duStatus ");
            params.put("duStatus", form.getDuStatus());
        }
        if (StringUtils.isNotBlank(form.getDuNo())) {
            sql.append("  AND do1.delivery_order_no = :duNo ");
            params.put("duNo", form.getDuNo());
        }
        if (StringUtils.isNotBlank(form.getTransactionType())) {
            sql.append("  AND do1.inventory_transaction_type = :transactionType ");
            params.put("transactionType", form.getTransactionType());
        }
        if (StringUtils.equals(CommonConstants.CHAR_Y, form.getUnfinishedOnly())) {
            sql.append("  AND do1.inventory_transaction_type IN (:inventoryTransactionTypeList) ");
            List<String> list = Arrays.asList(DeliveryStatus.CREATED,
                                              DeliveryStatus.ON_PICKING,
                                              DeliveryStatus.PACKING_COMPLETE,
                                              DeliveryStatus.ON_PACKING);
            params.put("inventoryTransactionTypeList", list);
        }
        return super.queryForList(sql.toString(), params, SPQ020601BO.class);
    }

    @Override
    public List<SPQ020602BO> getPickingInstructionDetailList(SPQ020602Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("   SELECT do1.from_facility_id            AS pointId            ");
        sql.append("        , mf.facility_cd                  AS pointCd            ");
        sql.append("        , mf.facility_nm                  AS pointNm            ");
        sql.append("        , pi2.picking_list_no             AS pickingListNo      ");
        sql.append("        , pi2.picking_list_no || '_' || pi2.seq_no AS pickingNo ");
        sql.append("        , do1.delivery_order_no           AS duNo               ");
        sql.append("        , COALESCE(do1.customer_cd, '')   AS customerCd         ");
        sql.append("        , COALESCE(do1.customer_nm, '')   AS customerNm         ");
        sql.append("        , do1.delivery_order_date         AS instructionDate    ");
        sql.append("        , do1.inventory_transaction_type  AS transactionTypeCd  ");
        sql.append("        , do1.delivery_status             AS duStatusCd         ");
        sql.append("        , pl.picking_list_id              AS pickingListId      ");
        sql.append("        , do1.delivery_order_id           AS deliveryOrderId    ");
        sql.append("        , pi2.seq_no                      AS seqNo              ");
        sql.append("        , pi2.location_cd                 AS locationCd         ");
        sql.append("        , pi2.product_cd                  AS productCd          ");
        sql.append("        , pi2.product_nm                  AS productNm          ");
        sql.append("        , pi2.qty                         AS pickingQty         ");
        sql.append("     FROM picking_list pl                                       ");
        sql.append("        , delivery_order do1                                    ");
        sql.append("        , picking_item pi2                                      ");
        sql.append("        , mst_facility mf                                       ");
        sql.append("    WHERE do1.delivery_order_id = pi2.delivery_order_id         ");
        sql.append("      AND pi2.picking_list_id = pl.picking_list_id              ");
        sql.append("      AND do1.from_facility_id = mf.facility_id                 ");
        sql.append("      AND do1.delivery_order_id = :deliveryOrderId              ");
        sql.append("      AND pl.picking_list_id = :pickingListId                   ");

        params.put("deliveryOrderId", form.getDeliveryOrderId());
        params.put("pickingListId", form.getPickingListId());
        return super.queryForList(sql.toString(), params, SPQ020602BO.class);
    }

    @Override
    public List<SPQ020601BO> getPartsPickingListReport(String siteId, SPQ020601Form form) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT wz.workzone_cd                      AS wz                 ");
        sql.append("          , l.location_cd                       AS locationCd         ");
        sql.append("          , doi.product_cd                      AS productCd          ");
        sql.append("          , doi.product_nm                      AS productNm          ");
        sql.append("          , COALESCE(pick.qty, 0)               AS pickQty            ");
        sql.append("          , pick.picking_list_no                AS pickSeqNo          ");
        sql.append("          , dor.delivery_order_no               AS duNo               ");
        sql.append("          , dor.delivery_order_id               AS duId               ");
        sql.append("          , mf.facility_cd                      AS pointCd            ");
        sql.append("          , mf.facility_nm                      AS pointName          ");
        sql.append("       FROM delivery_order_item doi                                   ");
        sql.append(" INNER JOIN delivery_order dor                                        ");
        sql.append("         ON doi.delivery_order_id = dor.delivery_order_id             ");
        sql.append(" INNER JOIN mst_facility mf                                           ");
        sql.append("         ON dor.from_facility_id = mf.facility_id                     ");
        sql.append("        AND dor.site_id = mf.site_id                                  ");
        sql.append(" INNER JOIN picking_item pick                                         ");
        sql.append("         ON pick.delivery_order_item_id = doi.delivery_order_item_id  ");
        sql.append(" INNER JOIN location l                                                ");
        sql.append("         ON l.location_id = pick.location_id                          ");
        sql.append("  LEFT JOIN workzone wz                                               ");
        sql.append("         ON l.workzone_id = wz.workzone_id                            ");
        sql.append("      WHERE pick.picking_list_id = :pickingListId                     ");

        params.put("pickingListId", form.getPickingListId());
        sql.append("   ORDER BY l.location_id                                             ");
        return super.queryForList(sql.toString(), params, SPQ020601BO.class);
    }

    @Override
    public SPQ020601PrintBO getPartsPickingListReportHeader(String siteId, SPQ020601Form form) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT mf.facility_cd || ' / ' || mf.facility_nm AS point             ");
        sql.append("          , pl.picking_list_no                        AS pickingListNo     ");
        sql.append("          , dor.delivery_order_no                     AS boxNo             ");
        sql.append("       FROM delivery_order dor                                             ");
        sql.append(" INNER JOIN picking_item picki                                             ");
        sql.append("         ON dor.delivery_order_id = picki.delivery_order_id                ");
        sql.append(" INNER JOIN picking_list pl                                                ");
        sql.append("         ON pl.picking_list_id = picki.picking_list_id                     ");
        sql.append(" INNER JOIN mst_facility mf                                                ");
        sql.append("         ON dor.from_facility_id = mf.facility_id                          ");
        sql.append("      WHERE pl.picking_list_id = :pickingListId                            ");
        sql.append("      LIMIT 1                                                              ");
        params.put("pickingListId", form.getPickingListId());
        return super.queryForSingle(sql.toString(), params, SPQ020601PrintBO.class);
    }
}
