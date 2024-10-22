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
@Table(name="service_request")
@Setter
@Getter
public class ServiceRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_request_id", unique=true, nullable=false)
    private Long serviceRequestId;

    @Column(name="request_dealer_cd", length=10)
    private String requestDealerCd;

    @Column(name="request_no", length=40)
    private String requestNo;

    @Column(name="request_date", length=8)
    private String requestDate;

    @Column(name="factory_receipt_no", length=40)
    private String factoryReceiptNo;

    @Column(name="factory_receipt_date", length=8)
    private String factoryReceiptDate;

    @Column(name="request_type", length=40)
    private String requestType;

    @Column(name="request_status", length=40)
    private String requestStatus;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="serialized_item_no", length=40)
    private String serializedItemNo;

    @Column(name="sold_date", length=8)
    private String soldDate;

    @Column(name="payment_month", length=8)
    private String paymentMonth;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="service_date", length=8)
    private String serviceDate;

    @Column(name="payment_parts_amt", precision=18)
    private BigDecimal paymentPartsAmt = BigDecimal.ZERO;

    @Column(name="payment_job_amt", precision=18)
    private BigDecimal paymentJobAmt = BigDecimal.ZERO;

    @Column(name="payment_total_amt", precision=18)
    private BigDecimal paymentTotalAmt = BigDecimal.ZERO;

    @Column(name="expiry_date", length=8)
    private String expiryDate;

    @Column(name="request_facility_id")
    private Long requestFacilityId;

    @Column(name="target_month", length=6)
    private String targetMonth;

    @Column(name="situation_happen_date", length=8)
    private String situationHappenDate;

    @Column(name="mileage", precision=18)
    private BigDecimal mileage = BigDecimal.ZERO;

    @Column(name="symptom_id")
    private Long symptomId;

    @Column(name="condition_id")
    private Long conditionId;

    @Column(name="problem_comment", length=256)
    private String problemComment;

    @Column(name="reason_comment", length=256)
    private String reasonComment;

    @Column(name="repair_comment", length=256)
    private String repairComment;

    @Column(name="shop_comment", length=256)
    private String shopComment;

    @Column(name="main_damage_parts_id")
    private Long mainDamagePartsId;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="authorization_no", length=40)
    private String authorizationNo;

    @Column(name="service_order_fault_id")
    private Long serviceOrderFaultId;

    @Column(name="campaign_no", length=40)
    private String campaignNo;

    @Column(name="bulletin_no", length=40)
    private String bulletinNo;
}
