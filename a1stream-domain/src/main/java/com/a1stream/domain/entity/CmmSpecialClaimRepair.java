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
@Table(name="cmm_special_claim_repair")
@Setter
@Getter
public class CmmSpecialClaimRepair extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_claim_repair_id", unique=true, nullable=false)
    private Long specialClaimRepairId;

    @Column(name="special_claim_id")
    private Long specialClaimId;

    @Column(name="repair_pattern", length=20)
    private String repairPattern;

    @Column(name="repair_type", length=40)
    private String repairType;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="main_damage_parts_flag", length=1)
    private String mainDamagePartsFlag;

    @Column(name="price", precision=18)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;


}
