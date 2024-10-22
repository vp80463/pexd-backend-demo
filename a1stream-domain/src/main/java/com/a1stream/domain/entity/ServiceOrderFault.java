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
@Table(name="service_order_fault")
@Setter
@Getter
public class ServiceOrderFault extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_fault_id", unique=true, nullable=false)
    private Long serviceOrderFaultId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="symptom_id")
    private Long symptomId;

    @Column(name="symptom_cd", length=40)
    private String symptomCd;

    @Column(name="symptom_nm", length=256)
    private String symptomNm;

    @Column(name="condition_id")
    private Long conditionId;

    @Column(name="condition_cd", length=40)
    private String conditionCd;

    @Column(name="condition_nm", length=256)
    private String conditionNm;

    @Column(name="warranty_claim_flag", length=1)
    private String warrantyClaimFlag;

    @Column(name="fault_start_date", length=8)
    private String faultStartDate;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="authorization_no", length=20)
    private String authorizationNo;

    @Column(name="repair_description", length=256)
    private String repairDescription;

    @Column(name="symptom_comment", length=256)
    private String symptomComment;

    @Column(name="condition_comment", length=256)
    private String conditionComment;

    @Column(name="process_comment", length=256)
    private String processComment;

    @Column(name="dealer_comment", length=256)
    private String dealerComment;


}
