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
@Table(name="sp_purchase_dw")
@Setter
@Getter
public class SpPurchaseDw {

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

    @Column(name="order_no", length=20)
    private String orderNo;

    @Column(name="order_priority_type", length=40)
    private String orderPriorityType;

    @Column(name="order_method_type", length=40)
    private String orderMethodType;

    @Column(name="purchase_type", length=40)
    private String purchaseType;

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

    @Column(name="po_qty", precision=18, scale=2)
    private BigDecimal poQty = BigDecimal.ZERO;

    @Column(name="po_cancel_qty", precision=18, scale=2)
    private BigDecimal poCancelQty = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18, scale=2)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="purchase_price", precision=18, scale=2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name="po_line", precision=18)
    private Integer poLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_cancel_line", precision=18)
    private Integer poCancelLine = CommonConstants.INTEGER_ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;

    @Column(name="amt", precision=18, scale=2)
    private BigDecimal amt = BigDecimal.ZERO;

    @Column(name="po_eo_line")
    private Integer poEoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_ro_line")
    private Integer poRoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_ho_line")
    private Integer poHoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_wo_line")
    private Integer poWoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_eo_amt", precision=18, scale=2)
    private BigDecimal poEoAmt = BigDecimal.ZERO;

    @Column(name="po_ro_amt", precision=18, scale=2)
    private BigDecimal poRoAmt = BigDecimal.ZERO;

    @Column(name="po_ho_amt", precision=18, scale=2)
    private BigDecimal poHoAmt = BigDecimal.ZERO;

    @Column(name="po_wo_amt", precision=18, scale=2)
    private BigDecimal poWoAmt = BigDecimal.ZERO;
}
