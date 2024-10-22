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
@Table(name="api_parts_sales_order")
@Setter
@Getter
public class ApiPartsSalesOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="crm_order_id", unique=true, nullable=false)
    private Long crmOrderId;

    @Column(name="crm_order_no", length=20)
    private String crmOrderNo;

    @Column(name="order_status", length=20)
    private String orderStatus;

    @Column(name="dealer_cd", nullable=false, length=10)
    private String dealerCd;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="crm_consumer_id", length=20)
    private String crmConsumerId;

    @Column(name="delivery_plan_date", length=8)
    private String deliveryPlanDate;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="point_id", length=20)
    private String pointId;

    @Column(name="party_id")
    private Long partyId;


}
