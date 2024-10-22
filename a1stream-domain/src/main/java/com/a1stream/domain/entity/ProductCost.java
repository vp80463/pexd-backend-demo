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
@Table(name="product_cost")
@Setter
@Getter
public class ProductCost extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_cost_id", unique=true, nullable=false)
    private Long productCostId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="cost", precision=18, scale=4)
    private BigDecimal cost = BigDecimal.ZERO;

    @Column(name="cost_type", length=40)
    private String costType;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
