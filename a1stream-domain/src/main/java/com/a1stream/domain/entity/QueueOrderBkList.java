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
@Table(name="queue_order_bk_list")
@Setter
@Getter
public class QueueOrderBkList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="db_id", unique=true, nullable=false)
    private Long dbId;

    @Column(name="order_id")
    private Long orderId;

    @Column(name="order_type", length=40)
    private String orderType;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="send_times")
    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    @Column(name="status", length=20)
    private String status;

    @Column(name="status_message", length=300)
    private String statusMessage;

}
