package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ReservationStatus;
import com.a1stream.domain.bo.service.SVM010701BO;
import com.a1stream.domain.custom.ServiceScheduleRepositoryCustom;
import com.a1stream.domain.form.service.SVM010701Form;
import com.a1stream.domain.form.service.SVM010702Form;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

import software.amazon.awssdk.utils.StringUtils;

public class ServiceScheduleRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements ServiceScheduleRepositoryCustom {

    @Override
    public List<SVM010701BO> findServiceReservationList(SVM010701Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append("     SELECT ss.reservation_status     as  reservationSts      ");
        sql.append("          , ss.schedule_date          as  reservationDate     ");
        sql.append("          , ss.schedule_time          as  reservationTime     ");
        sql.append("          , ss.service_schedule_no    as  reservationNo       ");
        sql.append("          , ss.plate_no               as  plateNo             ");
        sql.append("          , ss.product_nm             as  model               ");
        sql.append("          , ss.consumer_nm            as  consumer            ");
        sql.append("          , ss.mobile_phone           as  mobilePhone         ");
        sql.append("          , ss.service_contents       as  serviceBooking      ");
        sql.append("          , ss.memo                   as  memo                ");
        sql.append("          , ss.booking_method         as  reservationMethod   ");
        sql.append("          , ss.service_schedule_id    as  serviceScheduleId   ");
        sql.append("          , ss.service_order_id       as  serviceOrderId      ");
        sql.append("          , ss.consumer_id            as  consumerId          ");
        sql.append("          , ss.serialized_product_id  as  serializedProductId ");
        sql.append("          , ss.order_no               as  orderNo             ");
        sql.append("          , so.order_status_id        as  serviceStatus       ");
        sql.append("          , so.mechanic_nm            as  pic                 ");
        sql.append("       FROM service_schedule ss                               ");
        sql.append("  LEFT JOIN service_order so                                  ");
        sql.append("         ON so.service_order_id = ss.service_order_id         ");
        sql.append("      WHERE ss.site_id = :siteId                              ");
        sql.append("        AND ss.schedule_date >= :fromDate                     ");
        sql.append("        AND ss.schedule_date <= :toDate                       ");

        if(form.getPointId() != null) {
            sql.append("    AND ss.facility_id = :pointId                         ");
            params.put("pointId", form.getPointId());
        }

        if(!ObjectUtils.isEmpty(form.getPlateNo())) {
            sql.append("    AND ss.plate_no = :plateNo                            ");
            params.put("plateNo", form.getPlateNo());
        }

        if(!ObjectUtils.isEmpty(form.getConsumerNm())) {
            sql.append("    AND UPPER(ss.consumer_nm) like :consumerNm            ");
            params.put("consumerNm", StringUtils.upperCase(form.getConsumerNm())  + CommonConstants.CHAR_PERCENT);
        }

        if(!ObjectUtils.isEmpty(form.getMobilePhone())) {
            sql.append("    AND ss.mobile_phone = :mobilePhone                    ");
            params.put("mobilePhone", form.getMobilePhone());
        }

        if(!(form.getReservationSts().isEmpty() || form.getReservationSts().size() == 4)) {
            sql.append("    AND ss.reservation_status in (:status)                ");
            params.put("status", form.getReservationSts());
        }

        params.put("siteId", form.getSiteId());
        params.put("fromDate", form.getDateFrom());
        params.put("toDate", form.getDateTo());

        return super.queryForList(sql.toString(), params, SVM010701BO.class);
    }

    @Override
    public Integer getServiceScheduleRowCount(SVM010702Form form) {

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT count(1)                                  ");
        sql.append("   FROM service_schedule                          ");
        sql.append("  WHERE site_id = :siteId                         ");
        sql.append("    AND facility_id = :pointId                    ");
        sql.append("    AND schedule_date = :reservationDate          ");
        sql.append("    AND schedule_time = :reservationTime          ");
        sql.append("    AND reservation_status <> :reservationStatus  ");
        sql.append("    AND service_schedule_id <> :currentId         ");

        params.put("siteId", form.getSiteId());
        params.put("pointId", form.getPointId());
        params.put("reservationDate", form.getReservationDate());
        params.put("reservationTime", form.getReservationTime());
        params.put("reservationStatus", ReservationStatus.CANCELLED.getCodeDbid());
        params.put("currentId", form.getServiceScheduleId());

        return super.queryForSingle(sql.toString(), params, Integer.class);
    }
}