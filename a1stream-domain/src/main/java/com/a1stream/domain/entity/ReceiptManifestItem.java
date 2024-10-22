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
@Table(name="receipt_manifest_item")
@Setter
@Getter
public class ReceiptManifestItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_manifest_item_id", unique=true, nullable=false)
    private Long receiptManifestItemId;

    @Column(name="receipt_manifest_id")
    private Long receiptManifestId;

    @Column(name="receipt_product_cd", length=40)
    private String receiptProductCd;

    @Column(name="receipt_product_id")
    private Long receiptProductId;

    @Column(name="order_product_cd", length=40)
    private String orderProductCd;

    @Column(name="order_product_id")
    private Long orderProductId;

    @Column(name="receipt_qty", precision=18)
    private BigDecimal receiptQty = BigDecimal.ZERO;

    @Column(name="receipt_price", precision=18)
    private BigDecimal receiptPrice = BigDecimal.ZERO;

    @Column(name="stored_qty", precision=18)
    private BigDecimal storedQty = BigDecimal.ZERO;

    @Column(name="frozen_qty", precision=18)
    private BigDecimal frozenQty = BigDecimal.ZERO;

    @Column(name="purchase_order_no", length=40)
    private String purchaseOrderNo;

    @Column(name="order_priority_type", length=40)
    private String orderPriorityType;

    @Column(name="order_method_type", length=40)
    private String orderMethodType;

    @Column(name="case_no", length=10)
    private String caseNo;

    @Column(name="error_flag", length=1)
    private String errorFlag;

    @Column(name="error_info", length=256)
    private String errorInfo;

    @Column(name="manifest_item_status", length=40)
    private String manifestItemStatus;
}
