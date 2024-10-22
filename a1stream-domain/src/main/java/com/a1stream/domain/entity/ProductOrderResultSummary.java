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
@Table(name="product_order_result_summary")
@Setter
@Getter
public class ProductOrderResultSummary extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_order_result_summary_id", unique=true, nullable=false)
    private Long productOrderResultSummaryId;

    @Column(name="target_year", length=4)
    private String targetYear;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="month01_quantity", precision=18)
    private BigDecimal month01Quantity = BigDecimal.ZERO;

    @Column(name="month02_quantity", precision=18)
    private BigDecimal month02Quantity = BigDecimal.ZERO;

    @Column(name="month03_quantity", precision=18)
    private BigDecimal month03Quantity = BigDecimal.ZERO;

    @Column(name="month04_quantity", precision=18)
    private BigDecimal month04Quantity = BigDecimal.ZERO;

    @Column(name="month05_quantity", precision=18)
    private BigDecimal month05Quantity = BigDecimal.ZERO;

    @Column(name="month06_quantity", precision=18)
    private BigDecimal month06Quantity = BigDecimal.ZERO;

    @Column(name="month07_quantity", precision=18)
    private BigDecimal month07Quantity = BigDecimal.ZERO;

    @Column(name="month08_quantity", precision=18)
    private BigDecimal month08Quantity = BigDecimal.ZERO;

    @Column(name="month09_quantity", precision=18)
    private BigDecimal month09Quantity = BigDecimal.ZERO;

    @Column(name="month10_quantity", precision=18)
    private BigDecimal month10Quantity = BigDecimal.ZERO;

    @Column(name="month11_quantity", precision=18)
    private BigDecimal month11Quantity = BigDecimal.ZERO;

    @Column(name="month12_quantity", precision=18)
    private BigDecimal month12Quantity = BigDecimal.ZERO;


}
