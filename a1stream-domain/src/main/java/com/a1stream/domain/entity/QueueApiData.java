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
@Table(name="queue_api_data")
@Setter
@Getter
public class QueueApiData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="db_id", unique=true, nullable=false)
    private Long dbId;

    @Column(name="api_cd", length=100)
    private String apiCd;

    @Column(name="api_date", length=8)
    private String apiDate;

    @Column(name="process_key", length=100)
    private String processKey;

    @Column(name="message_lot_id", length=50)
    private String messageLotId;

    @Column(name="server_nm", length=100)
    private String serverNm;

    @Column(name="action_type", length=20)
    private String actionType;

    @Column(name="send_times")
    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    @Column(name="status", length=20)
    private String status;

    @Column(name="status_message", length=300)
    private String statusMessage;

    @Column(name="table_nm", length=200)
    private String tableNm;

    @Column(name="table_pk")
    private Long tablePk;

    @Column(name="relation_table_nm", length=200)
    private String relationTableNm;

    @Column(name="relation_table_pk", length=200)
    private String relationTablePk;


}
