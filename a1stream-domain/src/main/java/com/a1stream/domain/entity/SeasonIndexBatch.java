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
@Table(name="season_index_batch")
@Setter
@Getter
public class SeasonIndexBatch extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="season_index_batch_id", unique=true, nullable=false)
    private Long seasonIndexBatchId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="manual_flag", length=1)
    private String manualFlag;

    @Column(name="n_index", precision=18, scale = 2)
    private BigDecimal nIndex = BigDecimal.ZERO;

    @Column(name="n_1_index", precision=18, scale = 2)
    private BigDecimal n1Index = BigDecimal.ZERO;

    @Column(name="n_2_index", precision=18, scale = 2)
    private BigDecimal n2Index = BigDecimal.ZERO;

    @Column(name="n_3_index", precision=18, scale = 2)
    private BigDecimal n3Index = BigDecimal.ZERO;

    @Column(name="n_4_index", precision=18, scale = 2)
    private BigDecimal n4Index = BigDecimal.ZERO;

    @Column(name="n_5_index", precision=18, scale = 2)
    private BigDecimal n5Index = BigDecimal.ZERO;

    @Column(name="n_6_index", precision=18, scale = 2)
    private BigDecimal n6Index = BigDecimal.ZERO;

    @Column(name="n_7_index", precision=18, scale = 2)
    private BigDecimal n7Index = BigDecimal.ZERO;

    @Column(name="n_8_index", precision=18, scale = 2)
    private BigDecimal n8Index = BigDecimal.ZERO;

    @Column(name="n_9_index", precision=18, scale = 2)
    private BigDecimal n9Index = BigDecimal.ZERO;

    @Column(name="n_10_index", precision=18, scale = 2)
    private BigDecimal n10Index = BigDecimal.ZERO;

    @Column(name="n_11_index", precision=18, scale = 2)
    private BigDecimal n11Index = BigDecimal.ZERO;
}
