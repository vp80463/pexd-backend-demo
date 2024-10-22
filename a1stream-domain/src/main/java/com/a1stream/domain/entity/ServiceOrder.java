package com.a1stream.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name="service_order")
@Setter
@Getter
public class ServiceOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_id", unique=true, nullable=false)
    private Long serviceOrderId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="settle_date", length=8)
    private String settleDate;

    @Column(name="order_status_id", length=40)
    private String orderStatusId;

    @Column(name="order_status_content", length=256)
    private String orderStatusContent;

    @Column(name="service_category_id", length=40)
    private String serviceCategoryId;

    @Column(name="service_category_content", length=256)
    private String serviceCategoryContent;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="service_demand_content", length=256)
    private String serviceDemandContent;

    @Column(name="zero_km_flag", length=1)
    private String zeroKmFlag;

    @Column(name="mileage", precision=18)
    private BigDecimal mileage = BigDecimal.ZERO;

    @Column(name="start_time")
    private LocalDateTime startTime;

    @Column(name="finish_time")
    private LocalDateTime finishTime;

    @Column(name="operation_start_time")
    private LocalDateTime operationStartTime;

    @Column(name="operation_finish_time")
    private LocalDateTime operationFinishTime;

    @Column(name="mechanic_id")
    private Long mechanicId;

    @Column(name="mechanic_cd", length=40)
    private String mechanicCd;

    @Column(name="mechanic_nm", length=180)
    private String mechanicNm;

    @Column(name="cashier_id")
    private Long cashierId;

    @Column(name="cashier_cd", length=40)
    private String cashierCd;

    @Column(name="cashier_nm", length=180)
    private String cashierNm;

    @Column(name="entry_pic_id")
    private Long entryPicId;

    @Column(name="entry_pic_cd", length=40)
    private String entryPicCd;

    @Column(name="entry_pic_nm", length=180)
    private String entryPicNm;

    @Column(name="welcome_staff_id")
    private Long welcomeStaffId;

    @Column(name="welcome_staff_cd", length=40)
    private String welcomeStaffCd;

    @Column(name="welcome_staff_nm", length=180)
    private String welcomeStaffNm;

    @Column(name="cmm_serialized_product_id")
    private Long cmmSerializedProductId;

    @Column(name="brand_id")
    private Long brandId;

    @Column(name="brand_content", length=200)
    private String brandContent;

    @Column(name="plate_no", length=40)
    private String plateNo;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="engine_no", length=40)
    private String engineNo;

    @Column(name="color", length=256)
    private String color;

    @Column(name="ev_flag", length=1)
    private String evFlag;

    @Column(name="model_type_id")
    private Long modelTypeId;

    @Column(name="model_id")
    private Long modelId;

    @Column(name="model_cd", length=40)
    private String modelCd;

    @Column(name="model_nm", length=256)
    private String modelNm;

    @Column(name="sold_date", length=8)
    private String soldDate;

    @Column(name="use_type_id", length=40)
    private String useTypeId;

    @Column(name="relation_type_id", length=40)
    private String relationTypeId;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="consumer_vip_no", length=20)
    private String consumerVipNo;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="consumer_full_nm", length=180)
    private String consumerFullNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="email", length=100)
    private String email;

    @Column(name="service_subject", length=256)
    private String serviceSubject;

    @Column(name="mechanic_comment", length=256)
    private String mechanicComment;

    @Column(name="order_for_employee_flag", length=1)
    private String orderForEmployeeFlag;

    @Column(name="employee_cd", length=40)
    private String employeeCd;

    @Column(name="employee_relation_ship_id", length=40)
    private String employeeRelationShipId;

    @Column(name="ticket_no", length=40)
    private String ticketNo;

    @Column(name="payment_method_id", length=40)
    private String paymentMethodId;

    @Column(name="service_amt", precision=18)
    private BigDecimal serviceAmt = BigDecimal.ZERO;

    @Column(name="parts_amt", precision=18)
    private BigDecimal partsAmt = BigDecimal.ZERO;

    @Column(name="deposit_amt", precision=18)
    private BigDecimal depositAmt = BigDecimal.ZERO;

    @Column(name="related_sales_order_id")
    private Long relatedSalesOrderId;

    @Column(name="cmm_special_claim_id")
    private Long cmmSpecialClaimId;

    @Column(name="bulletin_no", length=20)
    private String bulletinNo;

    @Column(name="service_consumer_amt", precision=18)
    private BigDecimal serviceConsumerAmt = BigDecimal.ZERO;

    @Column(name="parts_consumer_amt", precision=18)
    private BigDecimal partsConsumerAmt = BigDecimal.ZERO;

    @Column(name="payment_amt", precision=18)
    private BigDecimal paymentAmt = BigDecimal.ZERO;
}
