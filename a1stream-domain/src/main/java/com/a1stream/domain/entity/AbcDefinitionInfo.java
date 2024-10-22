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
@Table(name="abc_definition_info")
@Setter
@Getter
public class AbcDefinitionInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="abc_definition_info_id", unique=true, nullable=false)
    private Long abcDefinitionInfoId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="abc_type", length=10)
    private String abcType;

    @Column(name="percentage", precision=6)
    private BigDecimal percentage = BigDecimal.ZERO;

    @Column(name="cost_from", precision=18, scale=4)
    private BigDecimal costFrom = BigDecimal.ZERO;

    @Column(name="cost_to", precision=18, scale=4)
    private BigDecimal costTo = BigDecimal.ZERO;

    @Column(name="target_supply_rate", precision=6)
    private BigDecimal targetSupplyRate = BigDecimal.ZERO;

    @Column(name="ssm_upper", precision=6)
    private BigDecimal ssmUpper = BigDecimal.ZERO;

    @Column(name="ssm_lower", precision=6)
    private BigDecimal ssmLower = BigDecimal.ZERO;

    @Column(name="rop_month", precision=6)
    private BigDecimal ropMonth = BigDecimal.ZERO;

    @Column(name="roq_month", precision=6)
    private BigDecimal roqMonth = BigDecimal.ZERO;

    @Column(name="add_leadtime")
    private Integer addLeadtime = CommonConstants.INTEGER_ZERO;

}
