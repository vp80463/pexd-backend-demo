package com.a1stream.domain.entity;

import java.io.Serial;
import java.math.BigDecimal;

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


/**
 * @author dong zhen
 */
@Entity
@Table(name="stock_location_transaction")
@Setter
@Getter
public class StockLocationTransaction extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="location_transaction_id", unique=true, nullable=false)
    private Long locationTransactionId;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="location_transaction_type", length=40)
    private String locationTransactionType;

    @Column(name="transaction_no", length=20)
    private String transactionNo;

    @Column(name="product_id")
    private Long productId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=200)
    private String productNm;

    @Column(name="person_id")
    private Long personId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="location_id")
    private Long locationId;

    @Column(name="move_qty", precision=18, scale = 2)
    private BigDecimal moveQty = BigDecimal.ZERO;

    @Column(name="transaction_date", length=8)
    private String transactionDate;

    @Column(name="transaction_time", length=8)
    private String transactionTime;

    @Column(name="relation_slip_id")
    private Long relationSlipId;

    @Column(name="relation_slip_item_id")
    private Long relationSlipItemId;

    @Column(name="reason", length=40)
    private String reason;
}
