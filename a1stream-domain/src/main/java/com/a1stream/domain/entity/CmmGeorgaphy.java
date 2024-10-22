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
@Table(name="cmm_georgaphy")
@Setter
@Getter
public class CmmGeorgaphy extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="geography_id", unique=true, nullable=false)
    private Long geographyId;

    @Column(name="geography_classification_id", length=40)
    private String geographyClassificationId;

    @Column(name="geography_cd", length=40)
    private String geographyCd;

    @Column(name="geography_nm", length=256)
    private String geographyNm;

    @Column(name="geography_full_nm", length=256)
    private String geographyFullNm;

    @Column(name="parent_geography_id")
    private Long parentGeographyId;
}
