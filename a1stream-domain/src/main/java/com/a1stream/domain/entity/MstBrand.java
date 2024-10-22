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
@Table(name="mst_brand")
@Setter
@Getter
public class MstBrand extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="brand_id", unique=true, nullable=false)
    private Long brandId;

    @Column(name="brand_cd", length=40)
    private String brandCd;

    @Column(name="brand_nm", length=200)
    private String brandNm;

    @Column(name="default_flag", nullable=false, length=1)
    private String defaultFlag;


}
