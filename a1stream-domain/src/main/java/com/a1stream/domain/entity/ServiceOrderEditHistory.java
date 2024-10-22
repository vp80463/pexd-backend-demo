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
@Table(name="service_order_edit_history")
@Setter
@Getter
public class ServiceOrderEditHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_edit_history_id", unique=true, nullable=false)
    private Long serviceOrderEditHistoryId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="operation_desc", length=256)
    private String operationDesc;

    @Column(name="process_time", length=29)
    private LocalDateTime processTime;

    @Column(name="cmm_operator_id")
    private Long cmmOperatorId;

    @Column(name="operator_cd", length=40)
    private String operatorCd;

    @Column(name="operator_nm", length=180)
    private String operatorNm;


}
