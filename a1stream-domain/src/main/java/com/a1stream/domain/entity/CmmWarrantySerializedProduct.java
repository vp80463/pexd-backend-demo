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
@Table(name="cmm_warranty_serialized_product")
@Setter
@Getter
public class CmmWarrantySerializedProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="warranty_serialized_product_id", unique=true, nullable=false)
    private Long warrantySerializedProductId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="warranty_product_classification", length=40)
    private String warrantyProductClassification;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="warranty_product_usage", length=40)
    private String warrantyProductUsage;

    @Column(name="comment", length=256)
    private String comment;


}
