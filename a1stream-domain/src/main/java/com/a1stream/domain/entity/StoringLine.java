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
@Table(name="storing_line")
@Setter
@Getter
public class StoringLine extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="storing_line_id", unique=true, nullable=false)
    private Long storingLineId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="storing_line_no", length=40)
    private String storingLineNo;

    @Column(name="storing_list_id")
    private Long storingListId;

    @Column(name="receipt_slip_item_id")
    private Long receiptSlipItemId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="case_no", length=10)
    private String caseNo;

    @Column(name="original_inst_qty", precision=18)
    private BigDecimal originalInstQty = BigDecimal.ZERO;

    @Column(name="instuction_qty", precision=18)
    private BigDecimal instuctionQty = BigDecimal.ZERO;

    @Column(name="stored_qty", precision=18)
    private BigDecimal storedQty = BigDecimal.ZERO;

    @Column(name="frozen_qty", precision=18)
    private BigDecimal frozenQty = BigDecimal.ZERO;

    @Column(name="completed_date", length=8)
    private String completedDate;

    @Column(name="completed_time", length=8)
    private String completedTime;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="receipt_slip_no", length=40)
    private String receiptSlipNo;
}
