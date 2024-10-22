package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.usertype.StringJsonUserType;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="remind_setting")
@Setter
@Getter
public class RemindSetting extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="remind_setting_id", unique=true, nullable=false)
    private Long remindSettingId;

    @Column(name="remind_type", length=40)
    private String remindType;

    @Column(name="remind_subject", length=40)
    private String remindSubject;

    @Column(name="step_content", length=40)
    private String stepContent;

    @Column(name="base_date_type", length=40)
    private String baseDateType;

    @Column(name="days", precision=18, scale=2)
    private BigDecimal days = BigDecimal.ZERO;

    @Column(name="continue_days", precision=18, scale=2)
    private BigDecimal continueDays = BigDecimal.ZERO;

    @Column(name="role_list")
    @Type(StringJsonUserType.class)
    private String roleList;
}
