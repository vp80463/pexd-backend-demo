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
@Table(name="product_stock_status")
@Setter
@Getter
public class ProductStockStatus extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_stock_status_id", unique=true, nullable=false)
    private Long productStockStatusId;

    @Column(name="facility_id", nullable=false)
    private Long facilityId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_stock_status_type", length=40)
    private String productStockStatusType;

    @Column(name="quantity", nullable=false, precision=18)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name="product_classification", length=40)
    private String productClassification;

}
