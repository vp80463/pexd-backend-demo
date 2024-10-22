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
@Table(name="service_package_item")
@Setter
@Getter
public class ServicePackageItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_package_item_id", unique=true, nullable=false)
    private Long servicePackageItemId;

    @Column(name="service_package_id")
    private Long servicePackageId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;


}
