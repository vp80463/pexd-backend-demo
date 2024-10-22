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
@Table(name="po_so_item_relation")
@Setter
@Getter
public class PoSoItemRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="po_so_item_relation_id", unique=true, nullable=false)
    private Long poSoItemRelationId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_abbr", length=256)
    private String facilityAbbr;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="supplier_id")
    private Long supplierId;

    @Column(name="supplier_cd", length=40)
    private String supplierCd;

    @Column(name="supplier_nm", length=100)
    private String supplierNm;

    @Column(name="po_no", length=40)
    private String poNo;

    @Column(name="so_no", length=40)
    private String soNo;

    @Column(name="purchase_order_id")
    private Long purchaseOrderId;

    @Column(name="sales_order_id")
    private Long salesOrderId;

    @Column(name="purchase_order_item_id")
    private Long purchaseOrderItemId;

    @Column(name="sales_order_item_id")
    private Long salesOrderItemId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="po_qty", precision=18)
    private BigDecimal poQty = BigDecimal.ZERO;

    @Column(name="so_qty", precision=18)
    private BigDecimal soQty = BigDecimal.ZERO;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
