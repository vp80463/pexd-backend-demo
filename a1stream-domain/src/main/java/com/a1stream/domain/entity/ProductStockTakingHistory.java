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
@Table(name="product_stock_taking_history")
@Setter
@Getter
public class ProductStockTakingHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_stock_taking_history_id", unique=true, nullable=false)
    private Long productStockTakingHistoryId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="started_date", length=8)
    private String startedDate;

    @Column(name="started_time", length=8)
    private String startedTime;

    @Column(name="finished_date", length=8)
    private String finishedDate;

    @Column(name="finished_time", length=8)
    private String finishedTime;

    @Column(name="logic_amt", precision=18)
    private BigDecimal logicAmt = BigDecimal.ZERO;

    @Column(name="physical_amt", precision=18)
    private BigDecimal physicalAmt = BigDecimal.ZERO;

    @Column(name="gap_amt", precision=18)
    private BigDecimal gapAmt = BigDecimal.ZERO;

    @Column(name="equal_lines")
    private Long equalLines;

    @Column(name="equal_items")
    private Long equalItems;

    @Column(name="equal_qty", precision=18)
    private BigDecimal equalQty = BigDecimal.ZERO;

    @Column(name="equal_amt", precision=18)
    private BigDecimal equalAmt = BigDecimal.ZERO;

    @Column(name="exceed_lines")
    private Long exceedLines;

    @Column(name="exceed_items")
    private Long exceedItems;

    @Column(name="exceed_qty", precision=18)
    private BigDecimal exceedQty = BigDecimal.ZERO;

    @Column(name="exceed_amt", precision=18)
    private BigDecimal exceedAmt = BigDecimal.ZERO;

    @Column(name="lack_lines")
    private Long lackLines;

    @Column(name="lack_items")
    private Long lackItems;

    @Column(name="lack_qty", precision=18)
    private BigDecimal lackQty = BigDecimal.ZERO;

    @Column(name="lack_amt", precision=18)
    private BigDecimal lackAmt = BigDecimal.ZERO;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
