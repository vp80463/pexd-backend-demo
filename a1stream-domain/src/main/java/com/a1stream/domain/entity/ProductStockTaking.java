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
@Table(name="product_stock_taking")
@Setter
@Getter
public class ProductStockTaking extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_stock_taking_id", unique=true, nullable=false)
    private Long productStockTakingId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="range_type", length=40)
    private String rangeType;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="workzone_id")
    private Long workzoneId;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="expected_qty", precision=18)
    private BigDecimal expectedQty = BigDecimal.ZERO;

    @Column(name="actual_qty", precision=18)
    private BigDecimal actualQty = BigDecimal.ZERO;

    @Column(name="input_flag", length=1)
    private String inputFlag;

    @Column(name="new_found_flag", length=1)
    private String newFoundFlag;

    @Column(name="current_average_cost", precision=18, scale=4)
    private BigDecimal currentAverageCost = BigDecimal.ZERO;

    @Column(name="pic_cd", length=40)
    private String picCd;

    @Column(name="pic_nm", length=60)
    private String picNm;

    @Column(name="started_date", length=8)
    private String startedDate;

    @Column(name="started_time", length=8)
    private String startedTime;

    @Column(name="finished_date", length=8)
    private String finishedDate;

    @Column(name="finished_time", length=8)
    private String finishedTime;

    @Column(name="product_classification", length=40)
    private String productClassification;


}
