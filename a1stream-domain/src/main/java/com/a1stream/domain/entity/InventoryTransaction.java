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
@Table(name="inventory_transaction")
@Setter
@Getter
public class InventoryTransaction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="inventory_transaction_id", unique=true, nullable=false)
    private Long inventoryTransactionId;

    @Column(name="physical_transaction_date", length=8)
    private String physicalTransactionDate;

    @Column(name="physical_transaction_time", length=8)
    private String physicalTransactionTime;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="from_organization_id")
    private Long fromOrganizationId;

    @Column(name="to_organization_id")
    private Long toOrganizationId;

    @Column(name="to_facility_id")
    private Long toFacilityId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="target_facility_id")
    private Long targetFacilityId;

    @Column(name="related_inventory_transaction_id", length=40)
    private String relatedInventoryTransactionId;

    @Column(name="related_slip_id")
    private Long relatedSlipId;

    @Column(name="related_slip_no", length=40)
    private String relatedSlipNo;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="inventory_transaction_nm", length=40)
    private String inventoryTransactionNm;

    @Column(name="in_qty", precision=18)
    private BigDecimal inQty = BigDecimal.ZERO;

    @Column(name="out_qty", precision=18)
    private BigDecimal outQty = BigDecimal.ZERO;

    @Column(name="current_qty", precision=18)
    private BigDecimal currentQty = BigDecimal.ZERO;

    @Column(name="in_cost", precision=18, scale=4)
    private BigDecimal inCost = BigDecimal.ZERO;

    @Column(name="out_cost", precision=18, scale=4)
    private BigDecimal outCost = BigDecimal.ZERO;

    @Column(name="current_average_cost", precision=18, scale=4)
    private BigDecimal currentAverageCost = BigDecimal.ZERO;

    @Column(name="stock_adjustment_reason_type", length=40)
    private String stockAdjustmentReasonType;

    @Column(name="stock_adjustment_reason_nm", length=100)
    private String stockAdjustmentReasonNm;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="location_cd", length=40)
    private String locationCd;

    @Column(name="reporter_id")
    private Long reporterId;

    @Column(name="reporter_nm", length=60)
    private String reporterNm;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
