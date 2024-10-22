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
@Table(name="demand_forecast")
@Setter
@Getter
public class DemandForecast extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="demand_forecast_id", unique=true, nullable=false)
    private Long demandForecastId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="target_month", length=6)
    private String targetMonth;

    @Column(name="to_product_id")
    private Long toProductId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="demand_quantity", precision=18, scale = 2)
    private BigDecimal demandQuantity = BigDecimal.ZERO;
}
