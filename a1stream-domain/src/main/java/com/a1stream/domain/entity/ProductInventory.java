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
@Table(name="product_inventory")
@Setter
@Getter
public class ProductInventory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_inventory_id", unique=true, nullable=false)
    private Long productInventoryId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="quantity", precision=18)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="product_quality_status_id", length=60)
    private String productQualityStatusId;

    @Column(name="primary_flag", length=1)
    private String primaryFlag;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
