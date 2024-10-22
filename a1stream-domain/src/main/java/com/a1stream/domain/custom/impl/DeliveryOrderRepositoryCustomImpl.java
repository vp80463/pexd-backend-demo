package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants.DeliveryStatus;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.InventoryTransactionType;
import com.a1stream.domain.bo.parts.SPM020901BO;
import com.a1stream.domain.bo.parts.SPM021101BO;
import com.a1stream.domain.bo.parts.SPM030602BO;
import com.a1stream.domain.custom.DeliveryOrderRepositoryCustom;
import com.a1stream.domain.form.parts.SPM020901Form;
import com.a1stream.domain.form.parts.SPM030602Form;
import com.a1stream.domain.vo.DeliveryOrderVO;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class DeliveryOrderRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements DeliveryOrderRepositoryCustom {

    @Override
    public List<SPM030602BO> getPartsPointTransferReceiptList(SPM030602Form form) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append("      SELECT dor.delivery_order_date             AS date               ");
        sql.append("           , dor.delivery_order_no               AS duNo               ");
        sql.append("           , dor.total_qty                       AS transferQty        ");
        sql.append("           , frm.facility_cd || ' ' || frm.facility_nm  AS fromPoint   ");
        sql.append("           , frm.facility_id                     AS fromPointId        ");
        sql.append("           , tom.facility_cd || ' ' || tom.facility_nm  AS toPoint     ");
        sql.append("           , tom.facility_id                     AS toPointId          ");
        sql.append("           , dor.delivery_order_id               AS deliveryOrderId    ");
        sql.append("           , dor.update_count                    AS updateCount        ");
        sql.append("        FROM delivery_order dor                                        ");
        sql.append("  INNER JOIN mst_facility frm                                          ");
        sql.append("          ON frm.facility_id = dor.from_facility_id                    ");
        sql.append("  INNER JOIN mst_facility tom                                          ");
        sql.append("          ON tom.facility_id = dor.to_facility_id                      ");
        sql.append("       WHERE dor.activity_flag = :activityFlag                         ");
        sql.append("         AND dor.inventory_transaction_type = :S027TRANSFEROUT         ");
        sql.append("         AND dor.delivery_status = :S029DISPATCHED                     ");
        sql.append("         AND dor.site_id = :siteId                                     ");
        sql.append("         AND dor.to_facility_id = :toPointId                           ");

        param.put("activityFlag", CommonConstants.CHAR_N);
        param.put("S027TRANSFEROUT", InventoryTransactionType.TRANSFEROUT.getCodeDbid());
        param.put("S029DISPATCHED", DeliveryStatus.DISPATCHED);
        param.put("siteId", form.getSiteId());
        param.put("toPointId", form.getToPointId());

        if (StringUtils.isNotBlank(form.getDate())) {
            sql.append(" AND dor.delivery_order_date = :date                       ");
            param.put("date", form.getDate());
        }
        if (StringUtils.isNotBlank(form.getDuNo())) {
            sql.append(" AND dor.delivery_order_no = :duNo                         ");
            param.put("duNo", form.getDuNo());
        }
        if (!Nulls.isNull(form.getFromPointId())) {
            sql.append(" AND dor.from_facility_id = :fromPointId                   ");
            param.put("fromPointId", form.getFromPointId());
        }
        return super.queryForList(sql.toString(), param, SPM030602BO.class);
    }

    @Override
    public List<SPM021101BO> findRePurchaseAndRetail(String siteId
                                                   , Long facilityId
                                                   , String deliveryStatus
                                                   , Long fromOrgnazationId
                                                   , Long toOrgnazationId
                                                   , String duNo) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("        SELECT do1.delivery_order_id             AS deliveryOrderId           ");
        sql.append("              ,do1.inventory_transaction_type    AS inventoryTransactionType  ");
        sql.append("              ,do1.customer_cd                   AS customerCd                ");
        sql.append("              ,do1.customer_nm                   AS customerNm                ");
        sql.append("              ,do1.customer_id                   AS customerId                ");
        sql.append("              ,do1.delivery_order_no             AS deliveryOrderNo           ");
        sql.append("              ,do1.total_amt                     AS totalAmt                  ");
        sql.append("              ,count(doi.delivery_order_item_id) AS lines                     ");
        sql.append("              ,do1.update_count AS updateCount                 ");
        sql.append("          FROM delivery_order do1                              ");
        sql.append("     LEFT JOIN delivery_order_item doi                         ");
        sql.append("            ON doi.delivery_order_id = do1.delivery_order_id   ");
        sql.append("           AND doi.site_id = do1.site_id                       ");
        sql.append("         WHERE from_facility_id = :facilityId                  ");
        sql.append("           AND do1.site_id = :siteId                           ");
        sql.append("           AND do1.delivery_status = :deliveryStatus           ");
        sql.append("           AND do1.from_organization_id = :fromOrgnazationId   "); //等待uccompany添加
        sql.append("           AND do1.to_organization_id = :toOrgnazationId       ");

        params.put("facilityId", facilityId);
        params.put("siteId", siteId);
        params.put("deliveryStatus", deliveryStatus);
        params.put("fromOrgnazationId", fromOrgnazationId);
        params.put("toOrgnazationId", toOrgnazationId);

        if(StringUtils.isNotEmpty(duNo)) {
            sql.append("           AND do1.delivery_order_no = :duNo           ");
            params.put("duNo", duNo);
        }

        sql.append("      GROUP BY do1.delivery_order_id                           ");
        sql.append("      ORDER BY do1.delivery_order_no ");
        return super.queryForList(sql.toString(), params, SPM021101BO.class);
    }

    @Override
    public List<SPM021101BO> findReturnAndTransfer(String siteId, Long facilityId, String deliveryStatus, String inventoryTransactionType, String duNo) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("        SELECT do1.delivery_order_id             AS deliveryOrderId           ");
        sql.append("              ,do1.inventory_transaction_type    AS inventoryTransactionType  ");
        sql.append("              ,mf.facility_cd                   AS customerCd                ");
        sql.append("              ,mf.facility_nm                   AS customerNm                ");
        sql.append("              ,do1.to_facility_id                   AS customerId                ");
        sql.append("              ,do1.delivery_order_no             AS deliveryOrderNo           ");
        sql.append("              ,do1.total_amt                     AS totalAmt                  ");
        sql.append("              ,count(doi.delivery_order_item_id) AS lines                     ");
        sql.append("              ,do1.update_count AS updateCount                     ");
        sql.append("          FROM delivery_order do1                              ");
        sql.append("     LEFT JOIN delivery_order_item doi                         ");
        sql.append("            ON doi.delivery_order_id = do1.delivery_order_id   ");
        sql.append("           AND doi.site_id = do1.site_id                       ");
        sql.append("     LEFT JOIN mst_facility mf                         ");
        sql.append("            ON mf.facility_id = do1.to_facility_id   ");
        sql.append("           AND mf.site_id = do1.site_id                       ");
        sql.append("         WHERE from_facility_id = :facilityId                  ");
        sql.append("           AND do1.site_id = :siteId                           ");
        sql.append("           AND do1.delivery_status = :deliveryStatus           ");
        sql.append("           AND do1.inventory_transaction_type <> :inventoryTransactionType  ");

        params.put("facilityId", facilityId);
        params.put("siteId", siteId);
        params.put("deliveryStatus", deliveryStatus);
        params.put("inventoryTransactionType", inventoryTransactionType);

        if(StringUtils.isNotEmpty(duNo)) {
            sql.append("           AND do1.delivery_order_no = :duNo           ");
            params.put("duNo", duNo);
        }

        sql.append("      GROUP BY do1.delivery_order_id,mf.facility_cd,mf.facility_nm,do1.to_facility_id ");
        sql.append("      ORDER BY do1.delivery_order_no ");
        return super.queryForList(sql.toString(), params, SPM021101BO.class);
    }

    @Override
    public List<SPM020901BO> findPickingDiscEntryList(SPM020901Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT pi2.product_id    AS partsId                   ");
        sql.append("         , pi2.product_cd    AS partsCd                   ");
        sql.append("         , pi2.product_nm    AS partsNm                   ");
        sql.append("         , pss.quantity      AS onHandQty                 ");
        sql.append("         , pi2.qty           AS instructionQty            ");
        sql.append("         , pi2.qty           AS actualQty                 ");
        sql.append("         , pi2.location_id   AS locationId                ");
        sql.append("         , pi2.updateCount   AS piUpdateCount             ");
        sql.append("      FROM delivery_order do2                             ");
        sql.append(" LEFT JOIN picking_item pi2                               ");
        sql.append("        ON do2.delivery_order_id = pi2.delivery_order_id  ");
        sql.append("       AND do2.site_id = pi2.site_id                      ");
        sql.append(" LEFT JOIN product_stock_status pss                       ");
        sql.append("        ON pss.product_id =p i2.product_id                ");
        sql.append("       AND pss.product_stock_status_type =: onHand        ");
        sql.append("     WHERE do2.site_id = :siteId                          ");
        sql.append("       AND do2.from_facility_id =: facilityId             ");
        sql.append("       AND do2.delivery_order_no = :duNo                  ");

        params.put("onHand", PJConstants.SpStockStatus.ONHAND_QTY.getCodeDbid());
        params.put("siteId", form.getSiteId());
        params.put("facilityId", form.getPointId());
        params.put("duNo", form.getDuNo());

        if(StringUtils.isNotEmpty(form.getPickingSeqNo())) {
            sql.append("    AND pi2.seq_no=:pickingSeqNo           ");
            params.put("pickingSeqNo", form.getPickingSeqNo());
        }

        return super.queryForList(sql.toString(), params, SPM020901BO.class);
    }

    @Override
    public DeliveryOrderVO findDeliveryOrder(String siteId, Long facilityId, String seqNo) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT *                          ");
        sql.append("       FROM picking_item pi2                             ");
        sql.append(" INNER JOIN delivery_order do2                              ");
        sql.append("         ON pi2.delivery_order_id = do2.delivery_order_id   ");
        sql.append("        AND do2.from_facility_id = :facilityId                  ");
        sql.append("     WHERE pi2.site_id = :siteId                          ");
        sql.append("       AND pi2.seq_no = :seqno                  ");

        params.put("siteId", siteId);
        params.put("facilityId", facilityId);
        params.put("seqno", seqNo);

        return super.queryForSingle(sql.toString(), params, DeliveryOrderVO.class);
    }
}
