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
@Table(name="cmm_pdi_setting")
@Setter
@Getter
public class CmmPdiSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="pdi_setting_id", unique=true, nullable=false)
    private Long pdiSettingId;

    @Column(name="pdi_id")
    private Long pdiId;

    @Column(name="item_cd", length=256)
    private String itemCd;

    @Column(name="description", length=256)
    private String description;


}
