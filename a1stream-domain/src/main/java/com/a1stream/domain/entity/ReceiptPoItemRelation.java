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
@Table(name="receipt_po_item_relation")
@Setter
@Getter
public class ReceiptPoItemRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="receipt_po_item_relation_id", unique=true, nullable=false)
    private Long receiptPoItemRelationId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="order_item_id")
    private Long orderItemId;

    @Column(name="receipt_slip_item_id")
    private Long receiptSlipItemId;

    @Column(name="purchase_order_no", length=40)
    private String purchaseOrderNo;

    @Column(name="supplier_invoice_no", length=40)
    private String supplierInvoiceNo;

    @Column(name="receipt_qty", precision=18)
    private BigDecimal receiptQty = BigDecimal.ZERO;


}
