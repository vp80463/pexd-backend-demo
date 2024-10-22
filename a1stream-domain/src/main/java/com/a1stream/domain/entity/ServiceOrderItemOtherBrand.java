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
@Table(name="service_order_item_other_brand")
@Setter
@Getter
public class ServiceOrderItemOtherBrand extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_item_other_brand_id", unique=true, nullable=false)
    private Long serviceOrderItemOtherBrandId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="settle_type", length=40)
    private String settleType;

    @Column(name="service_category", length=40)
    private String serviceCategory;

    @Column(name="service_demand")
    private Long serviceDemand;

    @Column(name="item_content", length=256)
    private String itemContent;

    @Column(name="item_id")
    private Long itemId;

    @Column(name="item_cd", length=40)
    private String itemCd;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="std_manhour", precision=18)
    private BigDecimal stdManhour = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="order_qty", precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="discount", precision=18)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="tax_rate", precision=18)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="selling_price_not_vat", precision=18)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    @Column(name="actual_amt", precision=18)
    private BigDecimal actualAmt = BigDecimal.ZERO;

    @Column(name="actual_amt_not_vat", precision=18)
    private BigDecimal actualAmtNotVat = BigDecimal.ZERO;

}
