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
@Table(name="season_index_manual")
@Setter
@Getter
public class SeasonIndexManual extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="season_index_manual_id", unique=true, nullable=false)
    private Long seasonIndexManualId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="manual_flag", length=1)
    private String manualFlag;

    @Column(name="index_month01", precision=18, scale = 2)
    private BigDecimal indexMonth01 = BigDecimal.ZERO;

    @Column(name="index_month02", precision=18, scale = 2)
    private BigDecimal indexMonth02 = BigDecimal.ZERO;

    @Column(name="index_month03", precision=18, scale = 2)
    private BigDecimal indexMonth03 = BigDecimal.ZERO;

    @Column(name="index_month04", precision=18, scale = 2)
    private BigDecimal indexMonth04 = BigDecimal.ZERO;

    @Column(name="index_month05", precision=18, scale = 2)
    private BigDecimal indexMonth05 = BigDecimal.ZERO;

    @Column(name="index_month06", precision=18, scale = 2)
    private BigDecimal indexMonth06 = BigDecimal.ZERO;

    @Column(name="index_month07", precision=18, scale = 2)
    private BigDecimal indexMonth07 = BigDecimal.ZERO;

    @Column(name="index_month08", precision=18, scale = 2)
    private BigDecimal indexMonth08 = BigDecimal.ZERO;

    @Column(name="index_month09", precision=18, scale = 2)
    private BigDecimal indexMonth09 = BigDecimal.ZERO;

    @Column(name="index_month10", precision=18, scale = 2)
    private BigDecimal indexMonth10 = BigDecimal.ZERO;

    @Column(name="index_month11", precision=18, scale = 2)
    private BigDecimal indexMonth11 = BigDecimal.ZERO;

    @Column(name="index_month12", precision=18, scale = 2)
    private BigDecimal indexMonth12 = BigDecimal.ZERO;
}
