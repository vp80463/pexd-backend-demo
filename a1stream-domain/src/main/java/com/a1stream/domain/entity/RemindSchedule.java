package com.a1stream.domain.entity;

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
@Table(name="remind_schedule")
@Setter
@Getter
public class RemindSchedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="remind_schedule_id", unique=true, nullable=false)
    private Long remindScheduleId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="activity_flag", length=1)
    private String activityFlag;

    @Column(name="reminded_date", length=8)
    private String remindedDate;

    @Column(name="remind_due_date", length=8)
    private String remindDueDate;

    @Column(name="expire_date", length=8)
    private String expireDate;

    @Column(name="remind_type", length=40)
    private String remindType;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="remind_setting_id")
    private Long remindSettingId;

    @Column(name="remind_contents", length=256)
    private String remindContents;

    @Column(name="event_start_date", length=8)
    private String eventStartDate;

    @Column(name="event_end_date", length=8)
    private String eventEndDate;

    @Column(name="related_order_id")
    private Long relatedOrderId;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="role_list")
    @Type(StringJsonUserType.class)
    private String roleList;
}
