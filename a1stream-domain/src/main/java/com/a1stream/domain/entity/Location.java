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
@Table(name="location")
@Setter
@Getter
public class Location extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="location_id", unique=true, nullable=false)
    private Long locationId;

    @Column(name="facility_id", nullable=false)
    private Long facilityId;

    @Column(name="workzone_id", nullable=false)
    private Long workzoneId;

    @Column(name="location_cd", nullable=false, length=40)
    private String locationCd;

    @Column(name="location_type", nullable=false, length=40)
    private String locationType;

    @Column(name="bin_type_id", nullable=false)
    private Long binTypeId;

    @Column(name="primary_flag", length=1)
    private String primaryFlag;


}
