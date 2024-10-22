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
@Table(name="receipt_slip_item")
@Setter
@Getter
public class ReceiptSlipItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_slip_item_id", unique=true, nullable=false)
    private Long receiptSlipItemId;

    @Column(name="receipt_slip_id")
    private Long receiptSlipId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="receipt_qty", precision=18)
    private BigDecimal receiptQty = BigDecimal.ZERO;

    @Column(name="frozen_qty", precision=18)
    private BigDecimal frozenQty = BigDecimal.ZERO;

    @Column(name="receipt_price", precision=18)
    private BigDecimal receiptPrice = BigDecimal.ZERO;

    @Column(name="supplier_invoice_no", length=40)
    private String supplierInvoiceNo;

    @Column(name="purchase_order_no", length=40)
    private String purchaseOrderNo;

    @Column(name="case_no", length=10)
    private String caseNo;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="color_nm", length=200)
    private String colorNm;
}
