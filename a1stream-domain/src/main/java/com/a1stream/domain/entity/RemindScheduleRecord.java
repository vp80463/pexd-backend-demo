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
@Table(name="remind_schedule_record")
@Setter
@Getter
public class RemindScheduleRecord extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="remind_schedule_record_id", unique=true, nullable=false)
    private Long remindScheduleRecordId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="record_date", length=8)
    private String recordDate;

    @Column(name="record_subject", length=40)
    private String recordSubject;

    @Column(name="description", length=256)
    private String description;

    @Column(name="remind_schedule_id")
    private Long remindScheduleId;

    @Column(name="satisfaction_point", length=1)
    private String satisfactionPoint;
}
