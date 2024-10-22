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
@Table(name="product_order_result_history")
@Setter
@Getter
public class ProductOrderResultHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_order_result_history_id", unique=true, nullable=false)
    private Long productOrderResultHistoryId;

    @Column(name="target_year", length=4)
    private String targetYear;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="month01_quantity", precision=18, scale=2)
    private BigDecimal month01Qty = BigDecimal.ZERO;

    @Column(name="month02_quantity", precision=18, scale=2)
    private BigDecimal month02Qty = BigDecimal.ZERO;

    @Column(name="month03_quantity", precision=18, scale=2)
    private BigDecimal month03Qty = BigDecimal.ZERO;

    @Column(name="month04_quantity", precision=18, scale=2)
    private BigDecimal month04Qty = BigDecimal.ZERO;

    @Column(name="month05_quantity", precision=18, scale=2)
    private BigDecimal month05Qty = BigDecimal.ZERO;

    @Column(name="month06_quantity", precision=18, scale=2)
    private BigDecimal month06Qty = BigDecimal.ZERO;

    @Column(name="month07_quantity", precision=18, scale=2)
    private BigDecimal month07Qty = BigDecimal.ZERO;

    @Column(name="month08_quantity", precision=18, scale=2)
    private BigDecimal month08Qty = BigDecimal.ZERO;

    @Column(name="month09_quantity", precision=18, scale=2)
    private BigDecimal month09Qty = BigDecimal.ZERO;

    @Column(name="month10_quantity", precision=18, scale=2)
    private BigDecimal month10Qty = BigDecimal.ZERO;

    @Column(name="month11_quantity", precision=18, scale=2)
    private BigDecimal month11Qty = BigDecimal.ZERO;

    @Column(name="month12_quantity", precision=18, scale=2)
    private BigDecimal month12Qty = BigDecimal.ZERO;

    @Column(name="backup_date", length=8)
    private String backupDate;

}
