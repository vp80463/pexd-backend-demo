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
@Table(name="po_cancel_history")
@Setter
@Getter
public class PoCancelHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="po_cancel_history_id", unique=true, nullable=false)
    private Long poCancelHistoryId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="supplier_id")
    private Long supplierId;

    @Column(name="supplier_cd", length=40)
    private String supplierCd;

    @Column(name="supplier_nm", length=100)
    private String supplierNm;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="purchase_order_id")
    private Long purchaseOrderId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="cancel_qty", precision=18)
    private BigDecimal cancelQty = BigDecimal.ZERO;

    @Column(name="cancel_date", length=8)
    private String cancelDate;

    @Column(name="cancel_comment", length=256)
    private String cancelComment;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
