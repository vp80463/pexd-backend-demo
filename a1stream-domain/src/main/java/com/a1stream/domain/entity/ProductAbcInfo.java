package com.a1stream.domain.entity;

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
@Table(name="product_abc_info")
@Setter
@Getter
public class ProductAbcInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_abc_id", unique=true, nullable=false)
    private Long productAbcId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="abc_definition_id")
    private Long abcDefinitionId;

    @Column(name="abc_type", length=10)
    private String abcType;


}
