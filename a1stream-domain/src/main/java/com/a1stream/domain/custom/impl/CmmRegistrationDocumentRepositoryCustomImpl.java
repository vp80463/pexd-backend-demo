package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.bo.unit.SDQ040103BO;
import com.a1stream.domain.custom.CmmRegistrationDocumentRepositoryCustom;
import com.a1stream.domain.form.unit.SDQ040103Form;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmRegistrationDocumentRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmRegistrationDocumentRepositoryCustom {

    @Override
    public PageImpl<SDQ040103BO> pageWarrantyCardInfo(SDQ040103Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder query_sql = warrantyCardQuerySql();;
        StringBuilder condition_sql = warrantyCardConditionSql(model, params);

        params.put("siteId", siteId);
        params.put("dateFrom", model.getDateFrom());
        params.put("dateTo", model.getDateTo());

        String countSql = "SELECT COUNT(1) FROM ( " + "SELECT 1 " + condition_sql.toString() + ") AS subquery; ";
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        Integer count = super.queryForSingle(countSql, params, Integer.class);

        return new PageImpl<>(super.queryForPagingList(query_sql.append(condition_sql).toString(), params, SDQ040103BO.class, pageable), pageable, count);
    }

    @Override
    public List<SDQ040103BO> listWarrantyCardInfo(SDQ040103Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder query_sql = warrantyCardQuerySql();;
        StringBuilder condition_sql = warrantyCardConditionSql(model, params);

        params.put("siteId", siteId);
        params.put("dateFrom", model.getDateFrom());
        params.put("dateTo", model.getDateTo());

        return super.queryForList(query_sql.append(condition_sql).toString(), params, SDQ040103BO.class);
    }

	@Override
	public SDM040103BO getWarrantyCardBasicInfo(Long registrationDocumentId) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append("     SELECT mf.facility_nm        AS entryFacilityNm");
        sql.append("          , so.order_no           AS orderNo        ");
        sql.append("          , so.ship_date          AS deliveryDate   ");
        sql.append("          , so.order_date         AS salesDate      ");
        sql.append("          , cp.person_nm          AS picNm          ");
        sql.append("          , crd.sales_order_id    AS salesOrderId   ");
        sql.append("          , crd.serialized_product_id AS serializedProductId ");
        sql.append("          , so.site_id            AS siteId         ");
        sql.append("          , so.cmm_consumer_id    AS ownerConsumerId         ");
        sql.append("          , so.user_consumer_id   AS userConsumerId          ");
        sql.append("          , crd.owner_type        AS ownerTypeId             ");
        sql.append("          , crd.use_type          AS userTypeId              ");
        sql.append("          , so.payment_method_type AS ownerPaymentMethodId   ");
        sql.append("          , crd.purchase_type     AS purchaseTypeId          ");
        sql.append("          , crd.psv_brand_nm      AS previousBikeBrandId     ");
        sql.append("          , crd.p_bike_nm         AS previousBikeName        ");
        sql.append("          , crd.mt_at_id          AS previousBikeMtAt        ");
        sql.append("          , crd.family_num        AS familyNum               ");
        sql.append("          , crd.num_bike          AS bikeNum                 ");
        sql.append("          , crd.registration_document_id AS registrationDocumentId ");
        sql.append("       FROM cmm_registration_document crd                    ");
        sql.append(" INNER JOIN sales_order so  on so.sales_order_id = crd.sales_order_id");
        sql.append(" INNER JOIN mst_facility mf on mf.facility_id = so.entry_facility_id ");
        sql.append(" INNER JOIN cmm_person cp   on cp.person_id = so.entry_pic_id        ");
        sql.append("      WHERE crd.registration_document_id =:registrationDocumentId    ");

        params.put("registrationDocumentId", registrationDocumentId);

        return super.queryForSingle(sql.toString(), params, SDM040103BO.class);
	}

	private StringBuilder warrantyCardConditionSql(SDQ040103Form model, Map<String, Object> params) {

        StringBuilder condition_sql = new StringBuilder();

        condition_sql.append("         FROM cmm_registration_document crd                 ");
        condition_sql.append("    LEFT JOIN sales_order so                                ");
        condition_sql.append("           ON so.sales_order_id = crd.sales_order_id        ");
        condition_sql.append("    LEFT JOIN serialized_product sp                         ");
        condition_sql.append("           ON sp.serialized_product_id = crd.serialized_product_id");
        condition_sql.append("    LEFT JOIN mst_product mp                                ");
        condition_sql.append("           ON sp.product_id = mp.product_id                 ");
        condition_sql.append("     LEFT JOIN cmm_consumer cc                              ");
        condition_sql.append("            ON cc.consumer_id = crd.consumer_id             ");
        condition_sql.append("     LEFT JOIN mst_facility mf                              ");
        condition_sql.append("            ON mf.facility_id = crd.facility_id             ");
        condition_sql.append("         WHERE crd.site_id = :siteId                        ");
        condition_sql.append("           AND crd.registration_date >= :dateFrom           ");
        condition_sql.append("           AND crd.registration_date <= :dateTo             ");

        if (model.getPointId() != null) {
            condition_sql.append(" AND crd.facility_id = :pointId");
            params.put("pointId", model.getPointId());
        }
        if (model.getModelId() != null) {
            condition_sql.append(" AND sp.product_id = :modelId");
            params.put("modelId", model.getModelId());
        }
        if (StringUtils.isNotBlank(model.getFrameNo())) {
            condition_sql.append(" AND sp.frame_no = :frameNo");
            params.put("frameNo", model.getFrameNo());
        }
        if (StringUtils.isNotBlank(model.getPlateNo())) {
            condition_sql.append(" AND sp.plate_no = :plateNo");
            params.put("plateNo", model.getPlateNo());
        }
        if (StringUtils.isNotBlank(model.getEngineNo())) {
            condition_sql.append(" AND sp.engine_no = :engineNo");
            params.put("engineNo", model.getEngineNo());
        }
        if (StringUtils.isNotBlank(model.getMobileNo())) {
            condition_sql.append(" AND cc.mobile_phone = :mobileNo");
            params.put("mobileNo", model.getMobileNo());
        }

        if (model.getConsumerId() != null) {
            condition_sql.append(" AND cc.consumer_id = :consumerId");
            params.put("consumerId", model.getConsumerId());
        }
        if (StringUtils.isNotBlank(model.getColorName())) {
            condition_sql.append(" AND mp.color_nm = :colorNm");
            params.put("colorNm", model.getColorName());
        }

        condition_sql.append(" ORDER BY mf.facility_cd ASC, crd.registration_date ASC, mp.product_cd ASC");

        return condition_sql;
    }

    private StringBuilder warrantyCardQuerySql() {

        StringBuilder query_sql = new StringBuilder();

        query_sql.append("    SELECT crd.registration_document_id as registrationDocumentId  ");
        query_sql.append("         , crd.facility_id              as facility_id             ");
        query_sql.append("         , mf.facility_nm               as facilityNm              ");
        query_sql.append("         , crd.registration_date        as registrationDate        ");
        query_sql.append("         , crd.sales_order_id           as salesOrderId            ");
        query_sql.append("         , so.order_date                as orderDate               ");
        query_sql.append("         , sp.product_id                as productId               ");
        query_sql.append("         , mp.product_cd                as modelCd                 ");
        query_sql.append("         , mp.sales_description         as modelNm                 ");
        query_sql.append("         , mp.color_nm                  as colorNm                 ");
        query_sql.append("         , cc.consumer_full_nm          as consumerName            ");
        query_sql.append("         , sp.frame_no                  as frameNo                 ");
        query_sql.append("         , sp.engine_no                 as engineNo                ");
        query_sql.append("         , sp.plate_no                  as plateNo                 ");
        query_sql.append("         , cc.mobile_phone              as mobileNo                ");

        return query_sql;
    }
}
