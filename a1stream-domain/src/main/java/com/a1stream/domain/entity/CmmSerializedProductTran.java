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
@Table(name="cmm_serialized_product_tran")
@Setter
@Getter
public class CmmSerializedProductTran extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="serialized_product_transaction_id", unique=true, nullable=false)
    private Long serializedProductTransactionId;

    @Column(name="serialized_product_id", nullable=false)
    private Long serializedProductId;

    @Column(name="transaction_date", length=8)
    private String transactionDate;

    @Column(name="transaction_time", length=8)
    private String transactionTime;

    @Column(name="related_slip_no", length=20)
    private String relatedSlipNo;

    @Column(name="reporter_nm", length=180)
    private String reporterNm;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="location_info_id")
    private Long locationInfoId;

    @Column(name="transaction_type_id", length=40)
    private String transactionTypeId;

    @Column(name="from_status", length=20)
    private String fromStatus;

    @Column(name="to_status", length=20)
    private String toStatus;

    @Column(name="product_id")
    private Long productId;

    @Column(name="from_party_id")
    private Long fromPartyId;

    @Column(name="to_party_id")
    private Long toPartyId;

    @Column(name="to_consumer_id")
    private Long toConsumerId;

    @Column(name="target_facility_id")
    private Long targetFacilityId;

    @Column(name="from_facility_id")
    private Long fromFacilityId;

    @Column(name="to_facility_id")
    private Long toFacilityId;

    @Column(name="in_cost", precision=18, scale=4)
    private BigDecimal inCost = BigDecimal.ZERO;

    @Column(name="out_cost", precision=18, scale=4)
    private BigDecimal outCost = BigDecimal.ZERO;

    @Column(name="out_price", precision=18)
    private BigDecimal outPrice = BigDecimal.ZERO;


}
