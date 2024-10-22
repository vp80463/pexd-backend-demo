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
@Table(name="cmm_person_facility")
@Setter
@Getter
public class CmmPersonFacility extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="person_facility_id", unique=true, nullable=false)
    private Long personFacilityId;

    @Column(name="person_id", nullable=false)
    private Long personId;

    @Column(name="facility_id", nullable=false)
    private Long facilityId;

    @Column(name="person_facility_type", length=40)
    private String personFacilityType;


}
