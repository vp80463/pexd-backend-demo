package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="cmm_service_demand")
@Setter
@Getter
public class CmmServiceDemand extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_demand_id", unique=true, nullable=false)
    private Long serviceDemandId;

    @Column(name="description", length=256)
    private String description;

    @Column(name="base_date_type", length=40)
    private String baseDateType;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="service_category", length=40)
    private String serviceCategory;

    @Column(name="due_period")
    private Integer duePeriod = CommonConstants.INTEGER_ZERO;

    @Column(name="base_date_after")
    private Integer baseDateAfter = CommonConstants.INTEGER_ZERO;


}
