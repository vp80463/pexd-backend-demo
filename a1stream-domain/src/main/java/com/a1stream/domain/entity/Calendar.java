package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="calendar")
@Setter
@Getter
public class Calendar extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="calendar_info_id", unique=true, nullable=false)
    private Long calendarInfoId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="target_year_month", length=6)
    private String targetYearMonth;

    @Column(name="day_seq_id")
    private Integer daySeqId = CommonConstants.INTEGER_ZERO;

    @Column(name="working_day_flag", length=1)
    private String workingDayFlag;


}
