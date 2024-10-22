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
@Table(name="cmm_service_job_for_do")
@Setter
@Getter
public class CmmServiceJobForDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_job_for_do_id", unique=true, nullable=false)
    private Long serviceJobForDoId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="product_category_cd", length=40)
    private String productCategoryCd;

    @Column(name="product_category_nm", length=200)
    private String productCategoryNm;

    @Column(name="job_id")
    private Long jobId;

    @Column(name="job_cd", length=40)
    private String jobCd;

    @Column(name="job_nm", length=200)
    private String jobNm;

    @Column(name="job_retrieve", length=250)
    private String jobRetrieve;

    @Column(name="service_category", length=40)
    private String serviceCategory;

    @Column(name="man_hours", precision=10)
    private BigDecimal manHours = BigDecimal.ZERO;

    @Column(name="labour_cost", precision=18)
    private BigDecimal labourCost = BigDecimal.ZERO;

    @Column(name="total_cost", precision=18)
    private BigDecimal totalCost = BigDecimal.ZERO;
}
