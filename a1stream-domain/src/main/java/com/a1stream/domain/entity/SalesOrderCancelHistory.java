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
@Table(name="sales_order_cancel_history")
@Setter
@Getter
public class SalesOrderCancelHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="sales_order_cancel_history_id", unique=true, nullable=false)
    private Long salesOrderCancelHistoryId;

    @Column(name="sales_order_id", nullable=false)
    private Long salesOrderId;

    @Column(name="order_no", length=40)
    private String orderNo;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="entry_facility_id")
    private Long entryFacilityId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="cmm_consumer_id")
    private Long cmmConsumerId;

    @Column(name="consumer_nm_first", length=60)
    private String consumerNmFirst;

    @Column(name="consumer_nm_middle", length=60)
    private String consumerNmMiddle;

    @Column(name="consumer_nm_last", length=60)
    private String consumerNmLast;

    @Column(name="consumer_nm_full", length=180)
    private String consumerNmFull;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="sales_order_item_id", nullable=false)
    private Long salesOrderItemId;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="order_qty", nullable=false, precision=18)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="actual_qty", nullable=false, precision=18)
    private BigDecimal actualQty = BigDecimal.ZERO;

    @Column(name="cancel_qty", nullable=false, precision=18)
    private BigDecimal cancelQty = BigDecimal.ZERO;

    @Column(name="cancel_date", length=8)
    private String cancelDate;

    @Column(name="cancel_time", length=8)
    private String cancelTime;

    @Column(name="cancel_reason_type", length=40)
    private String cancelReasonType;

    @Column(name="cancel_pic_id")
    private Long cancelPicId;

    @Column(name="cancel_pic_nm", length=60)
    private String cancelPicNm;


}
