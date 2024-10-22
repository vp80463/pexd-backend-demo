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
@Table(name="purchase_order_item")
@Setter
@Getter
public class PurchaseOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="purchase_order_item_id", unique=true, nullable=false)
    private Long purchaseOrderItemId;

    @Column(name="purchase_order_id", nullable=false)
    private Long purchaseOrderId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="order_qty", precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="on_purchase_qty", precision=18)
    private BigDecimal onPurchaseQty = BigDecimal.ZERO;

    @Column(name="actual_qty", precision=18)
    private BigDecimal actualQty = BigDecimal.ZERO;

    @Column(name="trans_qty", precision=18)
    private BigDecimal transQty = BigDecimal.ZERO;

    @Column(name="receive_qty", precision=18)
    private BigDecimal receiveQty = BigDecimal.ZERO;

    @Column(name="stored_qty", precision=18)
    private BigDecimal storedQty = BigDecimal.ZERO;

    @Column(name="cancelled_qty", precision=18)
    private BigDecimal cancelledQty = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="purchase_price", precision=18)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name="amount", precision=18)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name="actual_amt", precision=18)
    private BigDecimal actualAmt = BigDecimal.ZERO;

    @Column(name="bo_cancel_flag", length=1)
    private String boCancelFlag;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;


}
