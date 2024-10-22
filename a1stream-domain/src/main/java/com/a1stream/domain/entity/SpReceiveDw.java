package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="sp_receive_dw")
@Setter
@Getter
public class SpReceiveDw {

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="site_id", length=10)
    private String siteId;

    @Column(name="account_month", length=6)
    private String accountMonth;

    @Column(name="target_year", length=4)
    private String targetYear;

    @Column(name="target_month", length=2)
    private String targetMonth;

    @Column(name="target_day", length=8)
    private String targetDay;

    @Column(name="facility_cd", length=40)
    private String facilityCd;

    @Column(name="facility_nm", length=256)
    private String facilityNm;

    @Column(name="supplier_cd", length=40)
    private String supplierCd;

    @Column(name="supplier_nm", length=256)
    private String supplierNm;

    @Column(name="slip_no", length=40)
    private String slipNo;

    @Column(name="inventory_transaction_type_id", length=40)
    private String inventoryTransactionTypeId;

    @Column(name="product_cd", length=40)
    private String productCd;

    @Column(name="product_nm", length=256)
    private String productNm;

    @Column(name="middle_group_cd", length=20)
    private String middleGroupCd;

    @Column(name="middle_group_nm", length=200)
    private String middleGroupNm;

    @Column(name="large_group_cd", length=20)
    private String largeGroupCd;

    @Column(name="large_group_nm", length=200)
    private String largeGroupNm;

    @Column(name="product_classification", length=40)
    private String productClassification;

    @Column(name="abc_type", length=10)
    private String abcType;

    @Column(name="receive_qty", precision=18, scale=2)
    private BigDecimal receiveQty = BigDecimal.ZERO;

    @Column(name="receive_price", precision=18, scale=2)
    private BigDecimal receivePrice = BigDecimal.ZERO;

    @Column(name="receive_amt", precision=18, scale=2)
    private BigDecimal receiveAmt = BigDecimal.ZERO;

    @Column(name="receive_line", precision=10)
    private Integer receiveLine = CommonConstants.INTEGER_ZERO;

    @Column(name="order_method_type", length=40)
    private String orderMethodType;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
