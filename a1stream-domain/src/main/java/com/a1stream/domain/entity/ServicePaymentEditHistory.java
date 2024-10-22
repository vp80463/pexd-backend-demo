package com.a1stream.domain.entity;

import java.time.LocalDateTime;

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
@Table(name="service_payment_edit_history")
@Setter
@Getter
public class ServicePaymentEditHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_payment_edit_history_id", unique=true, nullable=false)
    private Long servicePaymentEditHistoryId;

    @Column(name="payment_id")
    private Long paymentId;

    @Column(name="payment_status", length=40)
    private String paymentStatus;

    @Column(name="change_date", length=29)
    private LocalDateTime changeDate;

    @Column(name="report_facility_id")
    private Long reportFacilityId;

    @Column(name="report_pic_id")
    private Long reportPicId;

    @Column(name="report_pic_cd", length=40)
    private String reportPicCd;

    @Column(name="report_pic_nm", length=180)
    private String reportPicNm;


}
