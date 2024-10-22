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
@Table(name="cmm_product_tax")
@Setter
@Getter
public class CmmProductTax extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="cmm_product_tax_id", unique=true, nullable=false)
    private Long cmmProductTaxId;

    @Column(name="cmm_product_id", nullable=false)
    private Long cmmProductId;

    @Column(name="product_classification", nullable=false, length=40)
    private String productClassification;

    @Column(name="tax_rate", nullable=false, precision=18)
    private BigDecimal taxRate = BigDecimal.ZERO;


}
