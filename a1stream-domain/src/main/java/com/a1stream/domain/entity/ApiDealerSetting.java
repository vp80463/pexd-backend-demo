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
@Table(name="api_dealer_setting")
@Setter
@Getter
public class ApiDealerSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="dl_api_setting_id", unique=true, nullable=false)
    private Long dlApiSettingId;

    @Column(name="dealer_cd", nullable=false, length=10)
    private String dealerCd;

    @Column(name="api_info_id", nullable=false)
    private Long apiInfoId;

    @Column(name="comment", length=256)
    private String comment;


}
