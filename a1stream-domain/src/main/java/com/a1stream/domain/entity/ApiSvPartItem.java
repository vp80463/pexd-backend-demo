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
@Table(name="api_sv_part_item")
@Setter
@Getter
public class ApiSvPartItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="crm_order_id", unique=true, nullable=false)
    private Long crmOrderId;

    @Column(name="parts_service_category", length=20)
    private String partsServiceCategory;

    @Column(name="parts_service_demand", length=20)
    private String partsServiceDemand;

    @Column(name="parts_id", length=20)
    private String partsId;

    @Column(name="parts_cd", length=20)
    private String partsCd;

    @Column(name="parts_nm", length=256)
    private String partsNm;

    @Column(name="discount", precision=18)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name="parts_service_symptom", length=20)
    private String partsServiceSymptom;

    @Column(name="api_sv_part_item_id", nullable=false, length=20)
    private String apiSvPartItemId;

    @Column(name="settle_type", length=20)
    private String settleType;

    @Column(name="std_price", precision=18)
    private BigDecimal stdPrice = BigDecimal.ZERO;


}
