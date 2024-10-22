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
@Table(name="parts_ropq_parameter")
@Setter
@Getter
public class PartsRopqParameter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="parts_ropq_parameter_id", unique=true, nullable=false)
    private Long partsRopqParameterId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="first_order_date", length=8)
    private String firstOrderDate;

    @Column(name="ropq_exception_sign", length=1)
    private String ropqExceptionSign;

    @Column(name="ropq_manual_sign", length=1)
    private String ropqManualSign;


}
