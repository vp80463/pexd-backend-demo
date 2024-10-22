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
@Table(name="purchase_order")
@Setter
@Getter
public class PurchaseOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="purchase_order_id", unique=true, nullable=false)
    private Long purchaseOrderId;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="cmm_facility_id")
    private Long cmmFacilityId;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="supplier_id")
    private Long supplierId;

    @Column(name="supplier_cd", length=40)
    private String supplierCd;

    @Column(name="supplier_nm", length=100)
    private String supplierNm;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="order_pic_id")
    private Long orderPicId;

    @Column(name="order_pic_nm", length=200)
    private String orderPicNm;

    @Column(name="approve_date", length=8)
    private String approveDate;

    @Column(name="approve_pic_id")
    private Long approvePicId;

    @Column(name="approve_pic_nm", length=200)
    private String approvePicNm;

    @Column(name="order_status", length=30)
    private String orderStatus;

    @Column(name="bo_cancel_flag", length=1)
    private String boCancelFlag;

    @Column(name="order_priority_type", length=40)
    private String orderPriorityType;

    @Column(name="order_method_type", length=40)
    private String orderMethodType;

    @Column(name="deliver_plan_date", length=8)
    private String deliverPlanDate;

    @Column(name="sales_order_no", length=40)
    private String salesOrderNo;

    @Column(name="supplier_order_no", length=40)
    private String supplierOrderNo;

    @Column(name="total_qty", precision=18)
    private BigDecimal totalQty = BigDecimal.ZERO;

    @Column(name="total_amount", precision=18)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name="total_actual_qty", precision=18)
    private BigDecimal totalActualQty = BigDecimal.ZERO;

    @Column(name="total_actual_amt", precision=18)
    private BigDecimal totalActualAmt = BigDecimal.ZERO;

    @Column(name="consignee_id", length=40)
    private String consigneeId;

    @Column(name="memo", length=256)
    private String memo;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="supplier_confirm_date", length=8)
    private String supplierConfirmDate;

    @Column(name="supplier_warehouse_cd", length=40)
    private String supplierWarehouseCd;

}
