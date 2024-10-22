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
@Table(name="mst_product_relation")
@Setter
@Getter
public class MstProductRelation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_relation_id", unique=true, nullable=false)
    private Long productRelationId;

    @Column(name="relation_type", length=40)
    private String relationType;

    @Column(name="from_product_id")
    private Long fromProductId;

    @Column(name="to_product_id")
    private Long toProductId;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="from_qty", precision=18)
    private BigDecimal fromQty = BigDecimal.ZERO;

    @Column(name="to_qty", precision=18)
    private BigDecimal toQty = BigDecimal.ZERO;


}
