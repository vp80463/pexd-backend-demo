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
@Table(name="queue_einvoice")
@Setter
@Getter
public class QueueEinvoice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="db_id", unique=true, nullable=false)
    private Long dbId;

    @Column(name="related_order_id")
    private Long relatedOrderId;

    @Column(name="related_order_no", length=40)
    private String relatedOrderNo;

    @Column(name="related_invoice_id")
    private Long relatedInvoiceId;

    @Column(name="related_invoice_no", length=40)
    private String relatedInvoiceNo;

    @Column(name="interf_code", length=40)
    private String interfCode;

    @Column(name="invoice_date", length=8)
    private String invoiceDate;

    @Column(name="server_nm", length=100)
    private String serverNm;

    @Column(name="send_times")
    private Integer sendTimes = CommonConstants.INTEGER_ZERO;

    @Column(name="status", length=20)
    private String status;

    @Column(name="status_message", length=300)
    private String statusMessage;

}
