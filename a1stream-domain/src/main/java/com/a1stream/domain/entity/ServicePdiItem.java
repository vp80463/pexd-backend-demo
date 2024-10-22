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
@Table(name="service_pdi_item")
@Setter
@Getter
public class ServicePdiItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_pdi_item_id", unique=true, nullable=false)
    private Long servicePdiItemId;

    @Column(name="service_pdi_id")
    private Long servicePdiId;

    @Column(name="description", length=512)
    private String description;

    @Column(name="result_status", length=1)
    private String resultStatus;

    @Column(name="pdi_setting_id")
    private Long pdiSettingId;


}
