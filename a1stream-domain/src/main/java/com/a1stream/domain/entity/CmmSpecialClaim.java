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
@Table(name="cmm_special_claim")
@Setter
@Getter
public class CmmSpecialClaim extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_claim_id", unique=true, nullable=false)
    private Long specialClaimId;

    @Column(name="bulletin_no", length=40)
    private String bulletinNo;

    @Column(name="campaign_no", length=40)
    private String campaignNo;

    @Column(name="campaign_type", length=40)
    private String campaignType;

    @Column(name="campaign_title", length=300)
    private String campaignTitle;

    @Column(name="description", length=1500)
    private String description;

    @Column(name="effective_date", length=8)
    private String effectiveDate;

    @Column(name="expired_date", length=8)
    private String expiredDate;


}
