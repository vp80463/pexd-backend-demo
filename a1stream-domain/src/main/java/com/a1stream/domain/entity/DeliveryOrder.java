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
@Table(name="delivery_order")
@Setter
@Getter
public class DeliveryOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="delivery_order_id", unique=true, nullable=false)
    private Long deliveryOrderId;

    @Column(name="delivery_order_no", length=40)
    private String deliveryOrderNo;

    @Column(name="inventory_transaction_type", length=40)
    private String inventoryTransactionType;

    @Column(name="delivery_status", length=40)
    private String deliveryStatus;

    @Column(name="from_organization_id")
    private Long fromOrganizationId;

    @Column(name="to_organization_id")
    private Long toOrganizationId;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="to_facility_id")
    private Long toFacilityId;

    @Column(name="to_consumer_id")
    private Long toConsumerId;

    @Column(name="total_qty", precision=18)
    private BigDecimal totalQty = BigDecimal.ZERO;

    @Column(name="total_amt", precision=18)
    private BigDecimal totalAmt = BigDecimal.ZERO;

    @Column(name="activity_flag", length=1)
    private String activityFlag;

    @Column(name="sales_return_reason_id", length=40)
    private String salesReturnReasonId;

    @Column(name="delivery_order_date", length=8)
    private String deliveryOrderDate;

    @Column(name="packing_date", length=8)
    private String packingDate;

    @Column(name="finish_date", length=8)
    private String finishDate;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="consignee_id")
    private Long consigneeId;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="drop_ship_flag", length=1)
    private String dropShipFlag;

    @Column(name="order_to_type", length=40)
    private String orderToType;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consignee_person", length=20)
    private String consigneePerson;

    @Column(name="consignee_mobile_phone", length=60)
    private String consigneeMobilePhone;

    @Column(name="consignee_addr", length=200)
    private String consigneeAddr;

    @Column(name="cmm_consumer_id")
    private Long cmmConsumerId;

    @Column(name="consumer_vip_no", length=20)
    private String consumerVipNo;

    @Column(name="consumer_nm_first", length=60)
    private String consumerNmFirst;

    @Column(name="consumer_nm_middle", length=60)
    private String consumerNmMiddle;

    @Column(name="consumer_nm_last", length=60)
    private String consumerNmLast;

    @Column(name="consumer_nm_full", length=182)
    private String consumerNmFull;

    @Column(name="email", length=100)
    private String email;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="total_amt_not_vat", precision=18, scale=2)
    private BigDecimal totalAmtNotVat = BigDecimal.ZERO;

    @Column(name="entry_pic_id")
    private Long entryPicId;

    @Column(name="entry_pic_nm", length=60)
    private String entryPicNm;

    @Column(name="pick_pic_id")
    private Long pickPicId;

    @Column(name="customer_cd", length=256)
    private String customerCd;
}
