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
@Table(name="cmm_special_claim_stamping")
@Setter
@Getter
public class CmmSpecialClaimStamping extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="special_claim_stamping_id", unique=true, nullable=false)
    private Long specialClaimStampingId;

    @Column(name="special_claim_id")
    private Long specialClaimId;

    @Column(name="stamping_style", length=50)
    private String stampingStyle;

    @Column(name="serial_no_from", length=40)
    private String serialNoFrom;

    @Column(name="serial_no_to", length=40)
    private String serialNoTo;


}
