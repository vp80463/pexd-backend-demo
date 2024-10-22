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
@Table(name="api_sv_fault_item")
@Setter
@Getter
public class ApiSvFaultItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="crm_order_id", unique=true, nullable=false)
    private Long crmOrderId;

    @Column(name="symptom", length=20)
    private String symptom;

    @Column(name="condition_cd", length=20)
    private String conditionCd;

    @Column(name="warranty_claim", length=1)
    private String warrantyClaim;

    @Column(name="fault_start_date", length=8)
    private String faultStartDate;

    @Column(name="main_damage_parts", length=20)
    private String mainDamageParts;

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

    @Column(name="api_sv_fault_item_id", length=20)
    private String apiSvFaultItemId;


}
