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
@Table(name="system_parameter")
@Setter
@Getter
public class SystemParameter extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="system_parameter_id", unique=true, nullable=false)
    private Long systemParameterId;

    @Column(name="parameter_value", nullable=false, length=256)
    private String parameterValue;

    @Column(name="system_parameter_type_id", nullable=false, length=40)
    private String systemParameterTypeId;

    @Column(name="facility_id")
    private Long facilityId;
}
