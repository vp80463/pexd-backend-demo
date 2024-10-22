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
@Table(name="cmm_warranty_model_part")
@Setter
@Getter
public class CmmWarrantyModelPart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="warranty_model_part_id", unique=true, nullable=false)
    private Long warrantyModelPartId;

    @Column(name="warranty_type", length=40)
    private String warrantyType;

    @Column(name="warranty_value", precision=18)
    private BigDecimal warrantyValue = BigDecimal.ZERO;

    @Column(name="model_cd", length=40)
    private String modelCd;

    @Column(name="model_id")
    private Long modelId;

    @Column(name="part_cd", length=40)
    private String partCd;

}
