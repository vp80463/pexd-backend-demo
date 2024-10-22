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
@Table(name="product_stock_status_backup")
@Setter
@Getter
public class ProductStockStatusBackup extends BaseEntity {

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

    @Column(name="qty", nullable=false, precision=18, scale=2)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="backup_date", length=8)
    private String backupDate;

}
