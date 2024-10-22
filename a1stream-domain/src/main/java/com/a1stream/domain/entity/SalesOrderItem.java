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
@Table(name="sales_order_item")
@Setter
@Getter
public class SalesOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="sales_order_item_id", unique=true, nullable=false)
    private Long salesOrderItemId;

    @Column(name="sales_order_id", nullable=false)
    private Long salesOrderId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="standard_price", nullable=false, precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="discount_off_rate", precision=18)
    private BigDecimal discountOffRate = BigDecimal.ZERO;

    @Column(name="selling_price", nullable=false, precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="tax_rate", precision=18)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="selling_price_not_vat", precision=18)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    @Column(name="actual_amt", precision=18)
    private BigDecimal actualAmt = BigDecimal.ZERO;

    @Column(name="discount_amt", precision=18)
    private BigDecimal discountAmt = BigDecimal.ZERO;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="order_qty", nullable=false, precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="actual_qty", nullable=false, precision=18)
    private BigDecimal actualQty = BigDecimal.ZERO;

    @Column(name="waiting_allocate_qty", nullable=false, precision=18)
    private BigDecimal waitingAllocateQty = BigDecimal.ZERO;

    @Column(name="allocated_qty", nullable=false, precision=18)
    private BigDecimal allocatedQty = BigDecimal.ZERO;

    @Column(name="instruction_qty", nullable=false, precision=18)
    private BigDecimal instructionQty = BigDecimal.ZERO;

    @Column(name="shipment_qty", nullable=false, precision=18)
    private BigDecimal shipmentQty = BigDecimal.ZERO;

    @Column(name="bo_qty", nullable=false, precision=18)
    private BigDecimal boQty = BigDecimal.ZERO;

    @Column(name="cancel_qty", nullable=false, precision=18)
    private BigDecimal cancelQty = BigDecimal.ZERO;

    @Column(name="cancel_date", length=8)
    private String cancelDate;

    @Column(name="cancel_time", length=8)
    private String cancelTime;

    @Column(name="cancel_reason_type", length=40)
    private String cancelReasonType;

    @Column(name="cancel_pic_id")
    private Long cancelPicId;

    @Column(name="cancel_pic_nm", length=60)
    private String cancelPicNm;

    @Column(name="allocated_product_id")
    private Long allocatedProductId;

    @Column(name="allocated_product_cd", length=40)
    private String allocatedProductCd;

    @Column(name="allocated_product_nm", length=200)
    private String allocatedProductNm;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="order_priority_seq")
    private Integer orderPrioritySeq = CommonConstants.INTEGER_ZERO;

    @Column(name="bo_cancel_flag", length=1)
    private String boCancelFlag;

    @Column(name="bo_ever_flag", length=1)
    private String boEverFlag;

    @Column(name="battery_flag", length=1)
    private String batteryFlag;

    @Column(name="battery_id")
    private Long batteryId;

    @Column(name="sv_claim_flag", length=1)
    private String svClaimFlag;

    @Column(name="sv_selling_price", precision=18)
    private BigDecimal svSellingPrice = BigDecimal.ZERO;

    @Column(name="service_category_id", length=40)
    private String serviceCategoryId;

    @Column(name="settle_type_id", length=40)
    private String settleTypeId;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="service_demand_content", length=256)
    private String serviceDemandContent;

    @Column(name="service_order_fault_id")
    private Long serviceOrderFaultId;

    @Column(name="symptom_id")
    private Long symptomId;

    @Column(name="service_package_id")
    private Long servicePackageId;

    @Column(name="service_package_cd", length=40)
    private String servicePackageCd;

    @Column(name="service_package_nm", length=256)
    private String servicePackageNm;

    @Column(name="actual_amt_not_vat", precision=18, scale=2)
    private BigDecimal actualAmtNotVat = BigDecimal.ZERO;

    @Column(name="battery_type", length=40)
    private String batteryType;

}
