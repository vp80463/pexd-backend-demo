package com.a1stream.domain.entity;

import java.math.BigDecimal;

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
@Table(name="cmm_service_history")
@Setter
@Getter
public class CmmServiceHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_history_id", unique=true, nullable=false)
    private Long serviceHistoryId;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="product_id")
    private Long productId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="plate_no", length=40)
    private String plateNo;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="service_category", length=40)
    private String serviceCategory;

    @Column(name="service_category_content", length=256)
    private String serviceCategoryContent;

    @Column(name="service_demand")
    private Long serviceDemand;

    @Column(name="service_demand_content", length=256)
    private String serviceDemandContent;

    @Column(name="service_subject", length=256)
    private String serviceSubject;

    @Column(name="mileage", precision=18)
    private BigDecimal mileage = BigDecimal.ZERO;

    @Column(name="operation_start_date", length=8)
    private String operationStartDate;

    @Column(name="operation_start_time", length=8)
    private String operationStartTime;

    @Column(name="operation_finish_date", length=8)
    private String operationFinishDate;

    @Column(name="operation_finish_time", length=8)
    private String operationFinishTime;

    @Column(name="mechanic_pic_nm", length=180)
    private String mechanicPicNm;

    @Column(name="job_amount", precision=18)
    private BigDecimal jobAmount = BigDecimal.ZERO;

    @Column(name="parts_amount", precision=18)
    private BigDecimal partsAmount = BigDecimal.ZERO;

    @Column(name="total_service_amount", precision=18)
    private BigDecimal totalServiceAmount = BigDecimal.ZERO;

    @Column(name="comment_for_customer", length=256)
    private String commentForCustomer;

    @Column(name="customer_comment", length=256)
    private String customerComment;

    @Column(name="replaced_part")
    private String replacedPart;
}
