package com.a1stream.domain.entity;

import java.io.Serial;

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

/**
 * @author Liu Chaoran
 */
@Entity
@Table(name="sys_additional_setting")
@Setter
@Getter
public class SysAdditionalSetting extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6501893578762278271L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="add_setting_id", unique=true, nullable=false)
    private Long addSettingId;

    @Column(name = "menu_id")
    private String menuId;

    @Column(name="menu_code", length=40)
    private String menuCode;

    @Column(name="menu_name", length=50)
    private String menuName;

    @Column(name="work_day_check", length=1)
    private String workDayCheck;

    @Column(name="account_month_check", length=1)
    private String accountMonthCheck;

    @Column(name="parts_stocktaking_check", length=1)
    private String partsStocktakingCheck;

    @Column(name="unit_stocktaking_check", length=1)
    private String unitStocktakingCheck;

}