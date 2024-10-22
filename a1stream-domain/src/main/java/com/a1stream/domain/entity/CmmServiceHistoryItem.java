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
@Table(name="cmm_service_history_item")
@Setter
@Getter
public class CmmServiceHistoryItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_history_item_id", unique=true, nullable=false)
    private Long serviceHistoryItemId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_content", length=256)
    private String productContent;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="qty", precision=18)
    private BigDecimal qty = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="amount", precision=18)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name="service_category", length=40)
    private String serviceCategory;

    @Column(name="service_demand")
    private Long serviceDemand;

    @Column(name="service_history_id")
    private Long serviceHistoryId;


}
