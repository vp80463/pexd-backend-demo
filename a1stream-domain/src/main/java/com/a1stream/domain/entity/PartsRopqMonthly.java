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
@Table(name="parts_ropq_monthly")
@Setter
@Getter
public class PartsRopqMonthly extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="parts_ropq_monthly_id", unique=true, nullable=false)
    private Long partsRopqMonthlyId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="ropq_type", length=40)
    private String ropqType;

    @Column(name="string_value", length=40)
    private String stringValue;


}
