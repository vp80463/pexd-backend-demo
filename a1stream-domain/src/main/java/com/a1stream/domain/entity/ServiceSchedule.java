package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="service_schedule")
@Setter
@Getter
public class ServiceSchedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_schedule_id", unique=true, nullable=false)
    private Long serviceScheduleId;

    @Column(name="service_schedule_no", length=20)
    private String serviceScheduleNo;

    @Column(name="schedule_date", length=8)
    private String scheduleDate;

    @Column(name="schedule_time", length=40)
    private String scheduleTime;

    @Column(name="pic_nm", length=180)
    private String picNm;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="plate_no", length=40)
    private String plateNo;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=256)
    private String productNm;

    @Column(name="memo", length=256)
    private String memo;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="order_no", length=20)
    private String orderNo;

    @Column(name="reservation_status", length=20)
    private String reservationStatus;

    @Column(name="service_contents", length=20)
    private String serviceContents;

    @Column(name="booking_method", length=20)
    private String bookingMethod;

    @Column(name="estimate_finish_time", length=4)
    private String estimateFinishTime;

    @Column(name="service_order_settled_flag", length=1)
    private String serviceOrderSettledFlag;


}
