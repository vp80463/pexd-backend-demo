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
@Table(name="reorder_guideline")
@Setter
@Getter
public class ReorderGuideline extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="reorder_guideline_id", unique=true, nullable=false)
    private Long reorderGuidelineId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="reorder_point", precision=10)
    private BigDecimal reorderPoint = BigDecimal.ZERO;

    @Column(name="reorder_qty", precision=18)
    private BigDecimal reorderQty = BigDecimal.ZERO;

    @Column(name="rop_roq_manual_flag", length=1)
    private String ropRoqManualFlag;

    @Column(name="parts_leadtime")
    private Integer partsLeadtime = CommonConstants.INTEGER_ZERO;

    @Column(name="facility_id")
    private Long facilityId;
}
