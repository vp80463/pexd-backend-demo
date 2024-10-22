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
@Table(name="cmm_special_claim_serial_pro")
@Setter
@Getter
public class CmmSpecialClaimSerialPro extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_claim_serial_pro_id", unique=true, nullable=false)
    private Long specialClaimSerialProId;

    @Column(name="special_claim_id")
    private Long specialClaimId;

    @Column(name="dealer_cd", length=10)
    private String dealerCd;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="repair_pattern", length=20)
    private String repairPattern;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="claim_flag", length=1)
    private String claimFlag;

    @Column(name="apply_date", length=8)
    private String applyDate;


}
