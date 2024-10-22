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
@Table(name="api_proformaorder")
@Setter
@Getter
public class ApiProformaorder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="api_proformaorder_id", unique=true, nullable=false)
    private Long apiProformaorderId;

    @Column(name="crm_order_id", length=20)
    private String crmOrderId;

    @Column(name="dealer_cd", nullable=false, length=10)
    private String dealerCd;

    @Column(name="crm_order_no", length=20)
    private String crmOrderNo;

    @Column(name="point_cd", length=20)
    private String pointCd;

    @Column(name="parts_id", length=20)
    private String partsId;

    @Column(name="parts_cd", length=20)
    private String partsCd;

    @Column(name="order_qty", precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;


}
