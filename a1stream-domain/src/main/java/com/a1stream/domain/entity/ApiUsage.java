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
@Table(name="api_usage")
@Setter
@Getter
public class ApiUsage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="api_usage_id", unique=true, nullable=false)
    private Long apiUsageId;

    @Column(name="access_key", nullable=false, length=128)
    private String accessKey;

    @Column(name="secret_key", nullable=false, length=128)
    private String secretKey;

    @Column(name="dealer_cd", nullable=false, length=10)
    private String dealerCd;

    @Column(name="local_sys_nm", nullable=false, length=100)
    private String localSysNm;

    @Column(name="status", nullable=false, length=1)
    private String status;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="description", length=256)
    private String description;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="dealer_type", length=40)
    private String dealerType;


}
