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
@Table(name="api_parts_sales_order_item")
@Setter
@Getter
public class ApiPartsSalesOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="parts_sales_order_item_id", unique=true, nullable=false)
    private Long partsSalesOrderItemId;

    @Column(name="crm_order_id", length=20)
    private String crmOrderId;

    @Column(name="parts_id", length=20)
    private String partsId;

    @Column(name="allocate_parts_cd", length=20)
    private String allocatePartsCd;

    @Column(name="superseding_cd", length=20)
    private String supersedingCd;

    @Column(name="order_qty", precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="std_price", precision=18)
    private BigDecimal stdPrice = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="discount", precision=18)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="amount", precision=18)
    private BigDecimal amount = BigDecimal.ZERO;


}
