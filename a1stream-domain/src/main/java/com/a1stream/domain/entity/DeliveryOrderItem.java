package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="delivery_order_item")
@Setter
@Getter
public class DeliveryOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="delivery_order_item_id", unique=true, nullable=false)
    private Long deliveryOrderItemId;

    @Column(name="delivery_order_id")
    private Long deliveryOrderId;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="original_delivery_qty", precision=18)
    private BigDecimal originalDeliveryQty = BigDecimal.ZERO;

    @Column(name="delivery_qty", precision=18)
    private BigDecimal deliveryQty = BigDecimal.ZERO;

    @Column(name="product_cost", precision=18, scale=4)
    private BigDecimal productCost = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="amt", precision=18)
    private BigDecimal amt = BigDecimal.ZERO;

    @Column(name="order_item_id")
    private Long orderItemId;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="sales_order_no", length=40)
    private String salesOrderNo;

    @Column(name="purchase_order_no", length=40)
    private String purchaseOrderNo;

    @Column(name="supplier_case_no", length=10)
    private String supplierCaseNo;

    @Column(name="selling_price_not_vat", precision=18, scale=2)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    @Column(name="tax_rate", precision=18, scale=2)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="amt_not_vat", precision=18, scale=2)
    private BigDecimal amtNotVat = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18, scale=2)
    private BigDecimal standardPrice = BigDecimal.ZERO;
}
