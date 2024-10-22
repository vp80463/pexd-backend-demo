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

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.BrandType;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;
import com.a1stream.domain.bo.common.EInvoiceProductsBO;
import com.a1stream.domain.bo.master.CMM010302BO;
import com.a1stream.domain.bo.service.SVM0102PrintBO;
import com.a1stream.domain.bo.service.SVM0109PrintBO;
import com.a1stream.domain.bo.service.SVM0120PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintBO;
import com.a1stream.domain.bo.service.SVM0130PrintServicePartBO;
import com.a1stream.domain.bo.service.SVQ010201BO;
import com.a1stream.domain.bo.service.SVQ010201ExportBO;
import com.a1stream.domain.bo.service.SVQ010401BO;
import com.a1stream.domain.custom.ServiceOrderRepositoryCustom;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.domain.form.service.SVQ010201Form;
import com.a1stream.domain.form.service.SVQ010401Form;
import com.a1stream.domain.logic.RepositoryLogic;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

public class ServiceOrderRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceOrderRepositoryCustom {

    @Resource
    private RepositoryLogic repositoryLogic;

    @Override
    public Page<SVQ010401BO> page0KmServiceOrder(SVQ010401Form model, String siteId) {

    	Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder();

        sql.append(" select so.order_date           as orderDate           ");
        sql.append("      , so.order_no             as orderNo             ");
        sql.append("      , so.order_status_content as orderStatus         ");
        sql.append("      , so.frame_no             as frameNo             ");
        sql.append("      , so.model_cd             as modelCd             ");
        sql.append("      , so.model_nm             as modelNm             ");
        sql.append("      , so.color                as color               ");
        sql.append("      , so.service_demand_content   as serviceDemand   ");
        sql.append("      , so.mechanic_nm          as mechanicName        ");
        sql.append("      , so.service_amt          as serviceAmount       ");
        sql.append("      , so.parts_amt            as partsAmount         ");
        sql.append("      , so.service_order_id     as serviceOrderId      ");
        sql.append("   FROM service_order so                               ");
        sql.append("  WHERE so.site_id = :siteId                           ");
        sql.append("    AND so.facility_id = :pointId                      ");

        params.put("siteId", siteId);
        params.put("pointId", model.getPointId());

        this.condition_0km_sql(model, sql, params);

        StringBuilder countSql = new StringBuilder();
        countSql.append(" SELECT COUNT(*) FROM ( ")
        		.append(" SELECT so.order_no FROM service_order so")
        		.append(" WHERE so.site_id = :siteId" )
        		.append(" AND so.facility_id = :pointId");
        this.condition_0km_sql(model, countSql, params);
        countSql.append(" ) AS subquery ");

        Integer count = super.queryForSingle(countSql.toString(), params, Integer.class);
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SVQ010401BO.class, pageable), pageable, count);
    }
    /**
     * SVQ0102_01 Service Order Inquiry
     */
    @Override
    public Page<SVQ010201BO> pageServiceOrder(SVQ010201Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();

        StringBuilder sql = new StringBuilder();

        sql.append(" select so.order_date           as orderDate           ");
        sql.append("      , so.order_no             as orderNo             ");
        sql.append("      , so.order_status_content as orderStatus         ");
        sql.append("      , so.consumer_vip_no      as consumerCd          ");
        sql.append("      , so.consumer_full_nm     as consumerNm          ");
        sql.append("      , so.last_nm              as lastNm              ");
        sql.append("      , so.middle_nm            as middleNm            ");
        sql.append("      , so.first_nm             as firstNm             ");
        sql.append("      , so.mobile_phone         as mobilePhone         ");
        sql.append("      , so.plate_no             as plateNo             ");
        sql.append("      , so.model_cd             as modelCd             ");
        sql.append("      , so.model_nm             as modelNm             ");
        sql.append("      , so.color                as color               ");
        sql.append("      , so.service_category_content as serviceCategory ");
        sql.append("      , so.service_demand_content   as serviceDemand   ");
        sql.append("      , so.mileage              as mileage             ");
        sql.append("      , so.cashier_nm           as receptionName       ");
        sql.append("      , so.mechanic_nm          as mechanicName        ");
        sql.append("      , so.service_amt          as serviceAmount       ");
        sql.append("      , so.parts_amt            as partsAmount         ");
        sql.append("      , so.brand_content        as brand               ");
        sql.append("      , so.settle_date          as settleDate          ");
        sql.append("      , so.brand_id             as brandId             ");
        sql.append("      , so.service_category_id  as serviceCategoryId   ");
        sql.append("      , so.service_order_id     as serviceOrderId      ");
        sql.append("   FROM service_order so                               ");
        sql.append("  WHERE so.site_id = :siteId                           ");
        sql.append("    AND so.facility_id = :pointId                      ");

        params.put("siteId", siteId);
        params.put("pointId", model.getPointId());

        this.condition_sql(model, sql, params);

        StringBuilder countSql = new StringBuilder();
        countSql.append(" SELECT COUNT(*) FROM ( ")
        		.append(" SELECT so.order_no FROM service_order so")
        		.append(" WHERE so.site_id = :siteId" )
        		.append(" AND so.facility_id = :pointId");
        this.condition_sql(model, countSql, params);
        countSql.append(" ) AS subquery ");

        Integer count = super.queryForSingle(countSql.toString(), params, Integer.class);
        Pageable pageable = PageRequest.of(model.getCurrentPage() - 1, model.getPageSize());

        return new PageImpl<>(super.queryForPagingList(sql.toString(), params, SVQ010201BO.class, pageable), pageable, count);
    }

    /**
     * SVQ0102_01 Service Order Inquiry Export
     */
    @Override
    public List<SVQ010201ExportBO> listServiceOrder(SVQ010201Form model, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" select so.order_date           as orderDate           ");
        sql.append("      , so.order_no             as orderNo             ");
        sql.append("      , so.order_status_content as orderStatus         ");
        sql.append("      , so.start_time           as startTime           ");
        sql.append("      , so.finish_time          as finishTime          ");
        sql.append("      , so.consumer_vip_no      as consumerCd          ");
        sql.append("      , so.consumer_full_nm     as consumerNm          ");
        sql.append("      , so.last_nm              as lastNm              ");
        sql.append("      , so.middle_nm            as middleNm            ");
        sql.append("      , so.first_nm             as firstNm             ");
        sql.append("      , so.address              as address             ");
        sql.append("      , so.mobile_phone         as mobilePhone         ");
        sql.append("      , so.plate_no             as plateNo             ");
        sql.append("      , so.frame_no             as frameNo             ");
        sql.append("      , so.engine_no            as engineNo            ");
        sql.append("      , so.model_cd             as modelCd             ");
        sql.append("      , so.model_nm             as modelNm             ");
        sql.append("      , so.color                as color               ");
        sql.append("      , so.sold_date            as soldDate            ");
        sql.append("      , so.service_category_content as serviceCategory ");
        sql.append("      , so.service_demand_content   as serviceDemand   ");
        sql.append("      , so.mileage              as mileage             ");
        sql.append("      , so.cashier_nm           as receptionName       ");
        sql.append("      , so.mechanic_nm          as mechanicName        ");
        sql.append("      , so.service_amt          as serviceAmount       ");
        sql.append("      , so.parts_amt            as partsAmount         ");
        sql.append("      , so.brand_content        as brand               ");
        sql.append("      , so.settle_date          as settleDate          ");
        sql.append("   FROM service_order so                               ");
        sql.append("  WHERE so.site_id = :siteId                           ");
        sql.append("    AND so.facility_id = :pointId                      ");

        params.put("siteId", siteId);
        params.put("pointId", model.getPointId());

        this.condition_sql(model, sql, params);

        return super.queryForList(sql.toString(), params, SVQ010201ExportBO.class);
    }

    /**
     * 待办列表
     */
    @Override
    public Integer getServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag, String serviceCategoryId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                  ");
        sql.append("   FROM service_order                             ");
        sql.append("  WHERE site_id = :siteId                         ");
        sql.append("    AND order_status_id = :orderStatus            ");
        sql.append("    AND zero_km_flag != :zeroKmFlag               ");
        sql.append("    AND service_category_id != :serviceCategoryId ");

        params.put("siteId", siteId);
        params.put("orderStatus", orderStatus);
        params.put("zeroKmFlag", zeroKmFlag);
        params.put("serviceCategoryId", serviceCategoryId);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    /**
     * 待办列表
     */
    @Override
    public Integer getZeroKmServiceOrderCount(String siteId, String orderStatus, String zeroKmFlag) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                       ");
        sql.append("   FROM service_order                  ");
        sql.append("  WHERE site_id = :siteId              ");
        sql.append("    AND order_status_id = :orderStatus ");
        sql.append("    AND zero_km_flag = :zeroKmFlag     ");

        params.put("siteId", siteId);
        params.put("orderStatus", orderStatus);
        params.put("zeroKmFlag", zeroKmFlag);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    /**
     * 待办列表
     */
    @Override
    public Integer getClaimBatteryServiceOrderCount(String siteId, String orderStatus, String serviceCategoryId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                 ");
        sql.append("   FROM service_order                            ");
        sql.append("  WHERE site_id = :siteId                        ");
        sql.append("    AND order_status_id = :orderStatus           ");
        sql.append("    AND service_category_id = :serviceCategoryId ");

        params.put("siteId", siteId);
        params.put("orderStatus", orderStatus);
        params.put("serviceCategoryId", serviceCategoryId);

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }

    @Override
    public List<CMM010302BO> getServiceDetailList(CMM010302Form form, String siteId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT order_date                 as  serviceDate           ");
        sql.append("          , order_no                   as  orderNo               ");
        sql.append("          , service_order_id           as  serviceOrderId        ");
        sql.append("          , brand_content              as  brand                 ");
        sql.append("          , plate_no                   as  plateNoByService      ");
        sql.append("          , service_category_content   as  serviceCategory       ");
        sql.append("          , service_category_id        as  serviceCategoryId     ");
        sql.append("          , service_demand_content     as  serviceDemand         ");
        sql.append("          , service_subject            as  serviceTitle          ");
        sql.append("          , service_amt                as  totalAmount           ");

        sql.append("       FROM service_order                                        ");

        sql.append("      WHERE consumer_id = :consumerId                            ");
        sql.append("        AND site_id = :siteId                                    ");
        sql.append("   ORDER BY order_no                                             ");

        params.put("consumerId", form.getConsumerId());
        params.put("siteId", siteId);

        return super.queryForList(sql.toString(), params, CMM010302BO.class);
    }

	private void condition_sql(SVQ010201Form model, StringBuilder sql, Map<String, Object> params) {
		// 订单类型：0km: zero_km_flag | 电池索赔：service_category_id=S012CLAIMBATTERY | 其他品牌：brand_id not in [1,8] |
		sql.append(" AND so.zero_km_flag != :zeroKmFlag ");
		params.put("zeroKmFlag", CommonConstants.CHAR_Y);
		// Battery Claim 和 Brand 互斥
		if (StringUtils.equals(model.getBatteryClaimFlag(), CommonConstants.CHAR_Y)) {
			sql.append(" AND so.service_category_id = :batteryClaim ");
			params.put("batteryClaim", ServiceCategory.CLAIMBATTERY.getCodeDbid());
		} else {
			if (StringUtils.equals(model.getBrand(), CommonConstants.CHAR_ONE)) {
				sql.append(" AND so.brand_id NOT IN (:yamahaBrand) ");
			} else {
				sql.append(" AND so.brand_id IN (:yamahaBrand) ");
			}
			params.put("yamahaBrand", List.of(Long.parseLong(BrandType.YAMAHA.getCodeDbid()), Long.parseLong(BrandType.YAMAHAS.getCodeDbid())));
		}

		// DateFrom
		if (StringUtils.isNotBlank(model.getDateFrom())) {
			if (StringUtils.equals(model.getDateType(), CommonConstants.CHAR_ONE)) {
				sql.append(" AND so.order_date >= :dateFrom");
			} else if (StringUtils.equals(model.getDateType(), CommonConstants.CHAR_TWO)) {
				sql.append(" AND so.settle_date >= :dateFrom");
			}

			params.put("dateFrom", model.getDateFrom());
		}
		// DateTo
		if (StringUtils.isNotBlank(model.getDateTo())) {

			if (StringUtils.equals(model.getDateType(), CommonConstants.CHAR_ONE)) {
				sql.append(" AND so.order_date <= :dateTo");
			} else if (StringUtils.equals(model.getDateType(), CommonConstants.CHAR_TWO)) {
				sql.append(" AND so.settle_date <= :dateTo");
			}

			params.put("dateTo", model.getDateTo());
		}
		// OrderStatus
		if (model.getOrderStatusList() != null && !model.getOrderStatusList().isEmpty()) {
			sql.append(" AND so.order_status_id in (:orderStatusList)");
			params.put("orderStatusList", model.getOrderStatusList());
		}
		// OrderNo
		if (StringUtils.isNotBlank(model.getOrderNo())) {
			sql.append(" AND so.order_no = :orderNo");
			params.put("orderNo", model.getOrderNo());
		}
		// ServiceCategory
		if (StringUtils.isNotBlank(model.getServiceCategoryId())) {
			sql.append(" AND so.service_category_id = :serviceCategoryId");
			params.put("serviceCategoryId", model.getServiceCategoryId());
		}
		// BatteryNo
		if (StringUtils.isNotBlank(model.getBatteryNo())) {
			sql.append(" AND EXISTS ( ");
			sql.append("    SELECT 1 FROM service_order_battery sob           ");
			sql.append("     WHERE sob.service_order_Id = so.service_order_id ");
			sql.append("       AND sob.battery_no = :batteryNo )              ");
			params.put("batteryNo", model.getBatteryNo());
		}
		// Consumer
		if (!Objects.isNull(model.getConsumerId())) {
			sql.append(" AND so.consumer_id = :consumerId");
			params.put("consumerId", model.getConsumerId());
		}
		// Mechanic
		if (!Objects.isNull(model.getMechanicId())) {
			sql.append(" AND so.mechanic_id = :mechanicId");
			params.put("mechanicId", model.getMechanicId());
		}
		// ReceptionPic
		if (!Objects.isNull(model.getReceptionPicId())) {
			sql.append(" AND so.cashier_id = :receptionPicId");
			params.put("receptionPicId", model.getReceptionPicId());
		}
		// Model
		if (!Objects.isNull(model.getModelId())) {
			sql.append(" AND so.model_id = :modelId");
			params.put("modelId", model.getModelId());
		}
		// ServiceJob
		if (!Objects.isNull(model.getServiceJobId())) {
			sql.append(" AND EXISTS ( ");
			sql.append("    SELECT 1 FROM service_order_job soj              ");
			sql.append("     WHERE soj.service_order_Id = so.service_order_id ");
			sql.append("       AND soj.job_id = :serviceJobId )              ");
			params.put("serviceJobId", model.getServiceJobId());
		}
		// PlateNo
		if (StringUtils.isNotBlank(model.getPlateNo())) {
			sql.append(" AND so.plate_no = :plateNo");
			params.put("plateNo", model.getPlateNo());
		}
		// FrameNo
		if (StringUtils.isNotBlank(model.getFrameNo())) {
			sql.append(" AND so.frame_no = :frameNo");
			params.put("frameNo", model.getFrameNo());
		}


		sql.append(" ORDER BY order_no DESC ");
	}

	private void condition_0km_sql(SVQ010401Form model, StringBuilder sql, Map<String, Object> params) {

		// 订单类型：0km: zero_km_flag
		sql.append(" AND so.zero_km_flag = :zeroKmFlag ");
		params.put("zeroKmFlag", CommonConstants.CHAR_Y);
		// DateFrom
		if (StringUtils.isNotBlank(model.getDateFrom())) {
			sql.append(" AND so.order_date >= :dateFrom");
			params.put("dateFrom", model.getDateFrom());
		}
		// DateTo
		if (StringUtils.isNotBlank(model.getDateTo())) {

			sql.append(" AND so.order_date <= :dateTo");
			params.put("dateTo", model.getDateTo());
		}
		// OrderStatus
		if (!model.getOrderStatusList().isEmpty()) {
			sql.append(" AND so.order_status_id in (:orderStatusList)");
			params.put("orderStatusList", model.getOrderStatusList());
		}
		// OrderNo
		if (StringUtils.isNotBlank(model.getOrderNo())) {
			sql.append(" AND so.order_no = :orderNo");
			params.put("orderNo", model.getOrderNo());
		}
		// Model
		if (!Objects.isNull(model.getModelId())) {
			sql.append(" AND so.model_id = :modelId");
			params.put("modelId", model.getModelId());
		}
		// Mechanic
		if (!Objects.isNull(model.getMechanicId())) {
			sql.append(" AND so.mechanic_id = :mechanicId");
			params.put("mechanicId", model.getMechanicId());
		}
		// ServiceJob
		if (!Objects.isNull(model.getServiceJobId())) {
			sql.append(" AND EXISTS ( ");
			sql.append("    SELECT 1 FROM service_order_job soj              ");
			sql.append("     WHERE soj.service_order_Id = so.service_order_id ");
			sql.append("       AND soj.job_id = :serviceJobId )              ");
			params.put("serviceJobId", model.getServiceJobId());
		}

		sql.append(" ORDER BY order_no DESC ");
	}

	@Override
    public SVM0102PrintBO getServiceJobCardHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo           ");
        sql.append("         , so.cashier_nm       AS receptionPic      ");
        sql.append("         , so.consumer_full_nm AS consumerName      ");
        sql.append("         , so.brand_content    AS brand      "       );
        sql.append("         , so.model_nm         AS modelName         ");
        sql.append("         , so.mileage          AS mileage           ");
        sql.append("         , so.plate_no         AS noPlate           ");
        sql.append("         , so.frame_no         AS frameNo           ");
        sql.append("         , so.mobile_phone     AS phoneNumber       ");
        sql.append("         , so.sold_date        AS soldDate          ");
        sql.append("         , so.order_date       AS serviceDate       ");
        sql.append("         , so.mechanic_nm      AS mechanicName      ");
        sql.append("         , mf.facility_nm      AS shopName          ");
        sql.append("         , mf.address1         AS address           ");
        sql.append("         , mf.contact_tel      AS telephone         ");
        sql.append("      FROM service_order so                         ");
        sql.append(" LEFT JOIN mst_facility mf                          ");
        sql.append("        ON mf.facility_id      = so.facility_id     ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId    ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0102PrintBO.class);
    }
    @Override
    public SVM0102PrintBO getServiceJobCardForDoHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo              ");
        sql.append("         , concat(mf.facility_nm, '(', mf.facility_cd, ')') AS shopName ");
        sql.append("         , mf.contact_tel      AS telephone            ");
        sql.append("         , mf.address1         AS address              ");
        sql.append("         , so.welcome_staff_nm AS welcomeStaff         ");
        sql.append("         , so.mechanic_nm      AS mechanicName         ");
        sql.append("         , so.consumer_full_nm AS consumerName         ");
        sql.append("         , so.mobile_phone     AS phoneNumber          ");
        sql.append("         , so.model_nm         AS modelName            ");
        sql.append("         , so.plate_no         AS noPlate              ");
        sql.append("         , so.frame_no         AS frameNo              ");
        sql.append("         , so.sold_date        AS soldDate             ");
        sql.append("         , so.order_date       AS serviceDate          ");
        sql.append("         , so.mileage          AS mileage              ");
        sql.append("      FROM service_order so                            ");
        sql.append(" LEFT JOIN mst_facility mf                             ");
        sql.append("        ON mf.facility_id = so.facility_id             ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId       ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0102PrintBO.class);
    }
    @Override
    public SVM0102PrintBO getServicePaymentHederData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo           ");
        sql.append("         , so.order_status_id  AS orderStatusId     ");
        sql.append("         , so.cashier_nm       AS cashier           ");
        sql.append("         , so.consumer_full_nm AS consumerName      ");
        sql.append("         , so.brand_content    AS brand             ");
        sql.append("         , so.model_nm         AS modelName         ");
        sql.append("         , so.mileage          AS mileage           ");
        sql.append("         , so.plate_no         AS noPlate           ");
        sql.append("         , so.frame_no         AS frameNo           ");
        sql.append("         , so.sold_date        AS soldDate          ");
        sql.append("         , so.order_date       AS serviceDate       ");
        sql.append("         , so.mechanic_nm      AS mechanicName      ");
        sql.append("         , so.mechanic_comment AS customerComment   ");
        sql.append("         , so.deposit_amt      AS depositAmt        ");
        sql.append("         , mf.facility_nm      AS shopName          ");
        sql.append("         , mf.address1         AS address           ");
        sql.append("         , mf.contact_tel      AS telephone         ");
        sql.append("      FROM service_order so                         ");
        sql.append(" LEFT JOIN mst_facility mf                          ");
        sql.append("        ON mf.facility_id = so.facility_id          ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId    ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0102PrintBO.class);
    }

    @Override
    public SVM0102PrintBO getServicePaymentForDoHederData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no          AS orderNo             ");
        sql.append("         , so.order_status_id   AS orderStatusId       ");
        sql.append("         , concat(mf.facility_nm, '(', mf.facility_cd, ')') AS shopName ");
        sql.append("         , mf.address1          AS address             ");
        sql.append("         , mf.contact_tel       AS telephone           ");
        sql.append("         , so.welcome_staff_nm  AS welcomeStaff        ");
        sql.append("         , so.mechanic_nm       AS mechanicName        ");
        sql.append("         , so.consumer_full_nm  AS consumerName        ");
        sql.append("         , so.mobile_phone      AS phoneNumber         ");
        sql.append("         , so.model_nm          AS modelName           ");
        sql.append("         , so.plate_no          AS noPlate             ");
        sql.append("         , so.frame_no          AS frameNo             ");
        sql.append("         , so.sold_date         AS soldDate            ");
        sql.append("         , so.order_date        AS serviceDate         ");
        sql.append("         , so.mileage           AS mileage             ");
        sql.append("         , so.employee_cd       AS employeeCode        ");
        sql.append("         , so.employee_relation_ship_id  AS relationShipId ");
        sql.append("         , so.ticket_no         AS ticketNo            ");
        sql.append("         , so.mechanic_comment  AS customerComment     ");
        sql.append("         , so.cashier_nm        AS cashier             ");
        sql.append("         , so.deposit_amt       AS depositAmt           ");
        sql.append("      FROM service_order so                            ");
        sql.append(" LEFT JOIN mst_facility mf                             ");
        sql.append("        ON mf.facility_id = so.facility_id             ");
        sql.append("      WHERE so.service_order_id = :serviceOrderId      ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0102PrintBO.class);
    }
    @Override
    public SVM0120PrintBO getOtherBrandBlankJobCardData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no          AS orderNo         ");
        sql.append("         , so.model_nm          AS modelName       ");
        sql.append("         , so.plate_no          AS noPlate         ");
        sql.append("         , so.consumer_full_nm  AS consumerName    ");
        sql.append("         , so.order_date        AS serviceDate     ");
        sql.append("      FROM service_order so                        ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0120PrintBO.class);
    }
    @Override
    public SVM0120PrintBO getOtherBrandJobCardHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("      SELECT so.order_no          AS orderNo          ");
        sql.append("           , so.cashier_nm        AS receptionPic     ");
        sql.append("           , brand_nm             AS brand            ");
        sql.append("           , so.model_nm          AS modelName        ");
        sql.append("           , so.mileage           AS mileage          ");
        sql.append("           , so.plate_no          AS noPlate          ");
        sql.append("           , so.frame_no          AS frameNo          ");
        sql.append("           , so.mechanic_nm       AS mechanicName     ");
        sql.append("           , so.consumer_vip_no   AS vipNo            ");
        sql.append("           , so.consumer_full_nm  AS consumerName     ");
        sql.append("           , so.order_date        AS serviceDate      ");
        sql.append("           , mf.facility_nm       AS shopName         ");
        sql.append("           , mf.address1          AS address          ");
        sql.append("           , mf.contact_tel       AS telephone        ");
        sql.append("      FROM service_order so                           ");
        sql.append(" LEFT JOIN mst_brand brand                            ");
        sql.append("        ON so.brand_id = brand.brand_id               ");
        sql.append(" LEFT JOIN mst_facility mf                            ");
        sql.append("        ON mf.facility_id = so.facility_id            ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId      ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0120PrintBO.class);
    }

    @Override
    public SVM0120PrintBO getOtherBrandPaymentHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT so.order_no         AS orderNo          ");
        sql.append("          , so.cashier_nm       AS cashier          ");
        sql.append("          , so.consumer_full_nm AS consumerName     ");
        sql.append("          , so.consumer_vip_no  AS vipNo            ");
        sql.append("          , so.brand_content    AS brand            ");
        sql.append("          , so.model_nm         AS modelName        ");
        sql.append("          , so.mileage          AS mileage          ");
        sql.append("          , so.plate_no         AS noPlate          ");
        sql.append("          , so.frame_no         AS frameNo          ");
        sql.append("          , so.order_date       AS serviceDate      ");
        sql.append("          , so.mechanic_nm      AS mechanicName     ");
        sql.append("          , so.mechanic_comment AS customerComment  ");
        sql.append("          , mf.facility_nm      AS shopName         ");
        sql.append("          , mf.address1         AS address          ");
        sql.append("          , mf.contact_tel      AS telephone        ");
        sql.append("      FROM service_order so                         ");
        sql.append(" LEFT JOIN mst_facility mf                          ");
        sql.append("        ON mf.facility_id = so.facility_id          ");
        sql.append(" WHERE so.service_order_id = :serviceOrderId        ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0120PrintBO.class);
    }

    @Override
    public SVM0130PrintBO get0KmJobCardHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no       AS orderNo             ");
        sql.append("         , so.cashier_nm     AS receptionPic        ");
        sql.append("         , so.model_nm       AS modelName           ");
        sql.append("         , so.frame_no       AS frameNo             ");
        sql.append("         , so.mobile_phone   AS phoneNumber         ");
        sql.append("         , so.mechanic_nm    AS mechanicName        ");
        sql.append("         , so.brand_content  AS brand               ");
        sql.append("         , so.order_date     AS serviceDate         ");
        sql.append("         , mf.facility_nm    AS shopName            ");
        sql.append("         , mf.address1       AS address             ");
        sql.append("         , mf.contact_tel    AS telephone           ");
        sql.append("      FROM service_order so                         ");
        sql.append(" LEFT JOIN mst_facility mf                          ");
        sql.append("        ON mf.facility_id = so.facility_id          ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId    ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0130PrintBO.class);
    }

    @Override
    public List<SVM0130PrintServicePartBO> get0KmJobCardPartList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT soi.allocated_product_cd AS partCd         ");
        sql.append("         , soi.allocated_product_nm AS partNm         ");
        sql.append("         , ''                       AS uom            ");
        sql.append("         , soi.actual_qty           AS qty            ");
        sql.append("      FROM sales_order so                             ");
        sql.append(" LEFT JOIN sales_order_item soi                       ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id     ");
        sql.append("     WHERE so.related_sv_order_id = :serviceOrderId   ");
        sql.append("       AND soi.actual_qty > 0                         ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0130PrintServicePartBO.class);
    }

    @Override
    public SVM0130PrintBO get0KmServicePaymentHeaderData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo           ");
        sql.append("         , so.order_status_id  AS orderStatusId     ");
        sql.append("         , so.cashier_nm       AS cashier           ");
        sql.append("         , so.brand_content    AS brand             ");
        sql.append("         , so.model_nm         AS modelName         ");
        sql.append("         , so.frame_no         AS frameNo           ");
        sql.append("         , so.mechanic_nm      AS mechanicName      ");
        sql.append("         , so.mechanic_comment AS customerComment   ");
        sql.append("         , so.order_date       AS serviceDate       ");
        sql.append("         , mf.facility_nm      AS shopName          ");
        sql.append("         , mf.address1         AS address           ");
        sql.append("         , mf.contact_tel      AS telephone         ");
        sql.append("         , 0                   AS depositAmt        ");
        sql.append("      FROM service_order so                         ");
        sql.append(" LEFT JOIN mst_facility mf                          ");
        sql.append("        ON mf.facility_id = so.facility_id          ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId    ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0130PrintBO.class);
    }

    @Override
    public List<SVM0130PrintServicePartBO> get0KmServicePaymentPartList(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT soi.allocated_product_cd        AS partCd        ");
        sql.append("         , soi.allocated_product_nm         AS partNm        ");
        sql.append("         , ''                               AS uom           ");
        sql.append("         , soi.selling_price                AS sellingPrice  ");
        sql.append("         , soi.actual_qty                   AS qty           ");
        sql.append("         , soi.selling_price*soi.actual_qty AS partAmount    ");
        sql.append("      FROM sales_order so                                    ");
        sql.append(" LEFT JOIN sales_order_item soi                              ");
        sql.append("        ON soi.sales_order_id = so.sales_order_id            ");
        sql.append("     WHERE so.related_sv_order_id = :serviceOrderId          ");
        sql.append("       AND soi.actual_qty > 0                                ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForList(sql.toString(), params, SVM0130PrintServicePartBO.class);
    }

    @Override
    public SVM0109PrintBO getServicePaymentData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo        ");
        sql.append("         , so.cashier_nm       AS receptionPic   ");
        sql.append("         , so.settle_date      AS finishedDate   ");
        sql.append("         , so.order_date       AS serviceDate    ");
        sql.append("         , so.mobile_phone     AS phoneNumber    ");
        sql.append("         , so.consumer_full_nm AS consumerName   ");
        sql.append("         , so.order_status_id  AS orderStatus    ");
        sql.append("         , mf.facility_nm      AS shopName       ");
        sql.append("         , mf.address1         AS address        ");
        sql.append("         , mf.contact_tel      AS telephone      ");
        sql.append("      FROM service_order so                      ");
        sql.append(" LEFT JOIN mst_facility mf                       ");
        sql.append("        ON mf.facility_id = so.facility_id       ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0109PrintBO.class);
    }

    @Override
    public SVM0109PrintBO getServicePaymentForDoData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo          ");
        sql.append("         , so.order_date       AS serviceDate      ");
        sql.append("         , so.cashier_nm       AS receptionPic     ");
        sql.append("         , so.welcome_staff_nm AS welcomeStaff     ");
        sql.append("         , so.settle_date      AS finishedDate     ");
        sql.append("         , so.mobile_phone     AS phoneNumber      ");
        sql.append("         , so.consumer_full_nm AS consumerName     ");
        sql.append("         , so.order_status_id  AS orderStatus      ");
        sql.append("         , so.cashier_nm       AS cashier          ");
        sql.append("         , concat(mf.facility_nm, '(', mf.facility_cd, ')') AS shopName ");
        sql.append("         , mf.address1         AS address          ");
        sql.append("         , mf.contact_tel      AS telephone        ");
        sql.append("         , con.address         AS consumerAddress  ");
        sql.append("      FROM service_order so                        ");
        sql.append(" LEFT JOIN mst_facility mf                         ");
        sql.append("        ON mf.facility_id = so.facility_id         ");
        sql.append(" LEFT JOIN cmm_consumer con                        ");
        sql.append("        ON con.consumer_id = so.consumer_id        ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0109PrintBO.class);
    }

    @Override
    public SVM0109PrintBO getJobCardData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo          ");
        sql.append("         , so.cashier_nm       AS receptionPic     ");
        sql.append("         , so.sold_date        AS soldDate         ");
        sql.append("         , so.order_date       AS serviceDate      ");
        sql.append("         , so.mobile_phone     AS phoneNumber      ");
        sql.append("         , so.consumer_full_nm AS consumerName     ");
        sql.append("         , mf.facility_nm      AS shopName         ");
        sql.append("         , mf.address1         AS address          ");
        sql.append("         , mf.contact_tel      AS telephone        ");
        sql.append("      FROM service_order so                        ");
        sql.append(" LEFT JOIN mst_facility mf                         ");
        sql.append("        ON mf.facility_id = so.facility_id         ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0109PrintBO.class);
    }

    @Override
    public SVM0109PrintBO getJobCardForDoData(Long serviceOrderId) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("    SELECT so.order_no         AS orderNo          ");
        sql.append("         , so.welcome_staff_nm AS welcomeStaff     ");
        sql.append("         , so.sold_date        AS soldDate         ");
        sql.append("         , so.order_date       AS serviceDate      ");
        sql.append("         , so.mobile_phone     AS phoneNumber      ");
        sql.append("         , so.consumer_full_nm AS consumerName     ");
        sql.append("         , con.address         AS consumerAddress  ");
        sql.append("         , concat(mf.facility_nm, '(', mf.facility_cd, ')') AS shopName ");
        sql.append("         , mf.address1         AS address          ");
        sql.append("         , mf.contact_tel      AS telephone        ");
        sql.append("      FROM service_order so                        ");
        sql.append(" LEFT JOIN mst_facility mf                         ");
        sql.append("        ON mf.facility_id = so.facility_id         ");
        sql.append(" LEFT JOIN cmm_consumer con                        ");
        sql.append("        ON con.consumer_id = so.consumer_id        ");
        sql.append("     WHERE so.service_order_id = :serviceOrderId   ");

        params.put("serviceOrderId", serviceOrderId);
        return super.queryForSingle(sql.toString(), params, SVM0109PrintBO.class);
    }

    @Override
    public EInvoiceInvoiceBO getInvoiceinfoForService(Long orderId,String updateProgram) { 
        
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT ((so.site_id_ || so.order_no)    AS cusCode,          ");
        sql.append("     replace(so.consumer_full_nm,'_',' ')    AS cusName,          ");
        sql.append("     so.order_no                             AS sophieuthu,       ");
        sql.append("     so.email                                AS extra,            ");
        sql.append("     so.email                                AS extra3,           ");
        sql.append("     (CASE WHEN so.model_type_id IS NULL THEN '' ELSE so.model_type_id END)   AS cusTaxCode,               ");
        sql.append("     (CASE WHEN so.model_type_id IS NULL THEN '' ELSE so.model_type_id END)   AS extra4,                   ");

        Set<String> updates= Set.of("SVM0102_01", "SVM0213_02", "timedtask", "dbupdate","SPM0303_01"); 

        if(updates.contains(updateProgram)){
            sql.append("  (CASE WHEN so.payment_method_id IS NULL THEN '' ELSE so.payment_method_id END)  AS paymentMethod, ");
        }
        if(StringUtils.equals(updateProgram, "SVM0120_01")){
            sql.append("  (CASE WHEN so.payment_method_id IS NULL THEN '' ELSE so.payment_method_id END)  AS paymentMethod, ");
        }

        sql.append("   so.mobile_phone              AS cusPhone,                ");
        sql.append("   so.order_no                  AS lenhDieuDong,            ");
        sql.append("   so.order_no                  AS saleOrder,               ");
        sql.append("   so.order_no                  AS billWay,                 ");
        sql.append("   case when so.consumer_id is null then '' else            ");
        sql.append("    ( (select cmm_consumer.address from cmm_consumer where cmm_consumer.consumer_id=so.customer_id) end AS cusAddress,    ");
        sql.append("   '0'                          AS sstSign,                 ");
        sql.append("  tax_rate                      AS vatRate,                 ");
        sql.append("   substring(invoice.invoice_date,7,2) || '/'|| substring(invoice.invoice_date,5,2) || '/' || substring(invoice.invoice_date,1,4) AS arisingDate,   ");
        sql.append("   invoice.last_updated_by      AS userID                   ");
        sql.append("   FROM  service_order so  ");
        sql.append("   left join sales_order on sales_order.related_sales_order_id=so.order_id ");
        sql.append("   left join invoice_item on invoice_item.sales_order_id=so.order_id       ");
        sql.append("   left join invoice on invoice.invoice_id=invoice_item.invoice_id         ");
        sql.append("   WHERE so.order_id_ = :orderId                                           ");
        params.put("orderId", orderId);
        sql.append(" GROUP BY cusCode, cusName, cusAddress, cusPhone, paymentMethod, arisingDate, vatRate , lenhDieuDong, saleOrder, sstSign, userID, billWay, extra,cusTaxCode,extra3,extra4,sophieuthu  ");

        return super.queryForSingle(sql.toString(), params, EInvoiceInvoiceBO.class);

    }
    @Override
    public List<EInvoiceProductsBO> getServiceProductsModels(Long orderId) {
        
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT job_cd     AS code,                                               ");
        sql.append(" job_nm            AS prodName,                                           ");
        sql.append(" ' '  AS prodUnit,                                                        ");
        sql.append(" oi.selling_price_not_vat AS prodPrice,                                   ");
        sql.append(" oi.selling_price_not_vat AS amount,                                      ");
        sql.append(" round(oi.discount_,0)    AS discount,                                    ");
        sql.append(" (case when oi.discount_ > 0 then 2 else 0 end)  AS isSum,                ");
        sql.append(" (case when oi.discount_ > 0 then selling_price_not_vat                   ");
        sql.append(" -  round(selling_price_not_vat*((100 - oi.discount_)/ 100) , 0)end)  AS  ");
        sql.append(" discountAmount ,                                                         ");
        sql.append(" '1'   AS prodQuantity                                                    ");
        sql.append(" FROM service_order_job oi                                                ");
        sql.append(" WHERE  oi.service_order_id_  =:orderId                                   ");
        sql.append(" and oi.settle_type_id_= 'S013CUSTOMER'                                   ");
        params.put("orderId", orderId);                                 

        return super.queryForList(sql.toString(), params, EInvoiceProductsBO.class);
    }
}
