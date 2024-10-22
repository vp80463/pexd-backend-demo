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
@Table(name="api_info")
@Setter
@Getter
public class ApiInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="api_info_id", unique=true, nullable=false)
    private Long apiInfoId;

    @Column(name="api_nm", nullable=false, length=100)
    private String apiNm;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="api_type", nullable=false, length=100)
    private String apiType;

    @Column(name="freequance", nullable=false, length=100)
    private String freequance;

    @Column(name="url", nullable=false, length=256)
    private String url;


}
