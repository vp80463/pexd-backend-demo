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
@Table(name="cmm_pricing_component")
@Setter
@Getter
public class CmmPricingComponent extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="pricing_component_id", unique=true, nullable=false)
    private Long pricingComponentId;

    @Column(name="price_list_id")
    private Long priceListId;

    @Column(name="price_category_id", length=40)
    private String priceCategoryId;

    @Column(name="order_type_id", length=40)
    private String orderTypeId;

    @Column(name="product_category_id", nullable=false)
    private Long productCategoryId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="price", nullable=false, precision=18)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;


}
