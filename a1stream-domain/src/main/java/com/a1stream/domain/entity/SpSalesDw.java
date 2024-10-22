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
@Table(name="sp_sales_dw")
@Setter
@Getter
public class SpSalesDw {

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

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="order_no", length=20)
    private String orderNo;

    @Column(name="order_source_type", length=40)
    private String orderSourceType;

    @Column(name="order_to_type", length=40)
    private String orderToType;

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

    @Column(name="standard_price", precision=18, scale=2)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18, scale=2)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="order_qty", precision=18, scale=2)
    private BigDecimal orderQty = BigDecimal.ZERO;

    @Column(name="actual_qty", precision=18, scale=2)
    private BigDecimal actualQty = BigDecimal.ZERO;

    @Column(name="allocate_qty", precision=18, scale=2)
    private BigDecimal allocateQty = BigDecimal.ZERO;

    @Column(name="bo_qty", precision=18, scale=2)
    private BigDecimal boQty = BigDecimal.ZERO;

    @Column(name="cancel_qty", precision=18, scale=2)
    private BigDecimal cancelQty = BigDecimal.ZERO;

    @Column(name="so_line", precision=10)
    private Integer soLine = CommonConstants.INTEGER_ZERO;

    @Column(name="allocate_line", precision=10)
    private Integer allocateLine = CommonConstants.INTEGER_ZERO;

    @Column(name="bo_line", precision=10)
    private Integer boLine = CommonConstants.INTEGER_ZERO;

    @Column(name="cancel_line", precision=10)
    private Integer cancelLine = CommonConstants.INTEGER_ZERO;

    @Column(name="compliance_line", precision=10)
    private Integer complianceLine = CommonConstants.INTEGER_ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;

    @Column(name="selling_price_not_vat", precision=18, scale=2)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;
}
