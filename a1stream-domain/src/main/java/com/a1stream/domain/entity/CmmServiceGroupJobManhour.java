package com.a1stream.domain.entity;

import java.math.BigDecimal;

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
@Table(name="cmm_service_group_job_manhour")
@Setter
@Getter
public class CmmServiceGroupJobManhour extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_group_job_manhour_id", unique=true, nullable=false)
    private Long serviceGroupJobManhourId;

    @Column(name="cmm_service_job_id")
    private Long cmmServiceJobId;

    @Column(name="service_group_id")
    private Long serviceGroupId;

    @Column(name="man_hours", precision=10)
    private BigDecimal manHours = BigDecimal.ZERO;

    @Column(name="labour_cost", precision=18)
    private BigDecimal labourCost = BigDecimal.ZERO;


}
