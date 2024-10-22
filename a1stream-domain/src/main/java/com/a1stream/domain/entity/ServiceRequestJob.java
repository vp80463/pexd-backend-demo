package com.a1stream.domain.entity;

import java.math.BigDecimal;

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
@Table(name="service_request_job")
@Setter
@Getter
public class ServiceRequestJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_request_job_id", unique=true, nullable=false)
    private Long serviceRequestJobId;

    @Column(name="service_request_id")
    private Long serviceRequestId;

    @Column(name="service_order_job_id")
    private Long serviceOrderJobId;

    @Column(name="job_id")
    private Long jobId;

    @Column(name="std_manhour", precision=18)
    private BigDecimal stdManhour = BigDecimal.ZERO;

    @Column(name="payment_amt", precision=18)
    private BigDecimal paymentAmt = BigDecimal.ZERO;

    @Column(name="select_flag", length=1)
    private String selectFlag;


}
