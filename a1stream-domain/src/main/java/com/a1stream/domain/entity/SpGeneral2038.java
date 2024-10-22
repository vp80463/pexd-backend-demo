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
@Table(name="sp_general_2038")
@Setter
@Getter
public class SpGeneral2038 {

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

    @Column(name="customer_cd", length=40)
    private String customerCd;

    @Column(name="customer_nm", length=256)
    private String customerNm;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="so_line", precision=10)
    private Integer soLine = CommonConstants.INTEGER_ZERO;

    @Column(name="allocate_line", precision=10)
    private Integer allocateLine = CommonConstants.INTEGER_ZERO;

    @Column(name="bo_line", precision=10)
    private Integer boLine = CommonConstants.INTEGER_ZERO;

    @Column(name="cancel_line", precision=10)
    private Integer cancelLine = CommonConstants.INTEGER_ZERO;

    @Column(name="shipment_line", precision=10)
    private Integer shipmentLine = CommonConstants.INTEGER_ZERO;

    @Column(name="return_line", precision=10)
    private Integer returnLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_line", precision=10)
    private Integer poLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_eo_line", precision=10)
    private Integer poEoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_ro_line", precision=10)
    private Integer poRoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_ho_line", precision=10)
    private Integer poHoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_wo_line", precision=10)
    private Integer poWoLine = CommonConstants.INTEGER_ZERO;

    @Column(name="po_cancel_line", precision=10)
    private Integer poCancelLine = CommonConstants.INTEGER_ZERO;

    @Column(name="receipt_line", precision=10)
    private Integer receiptLine = CommonConstants.INTEGER_ZERO;

    @Column(name="so_amt", precision=18, scale=2)
    private BigDecimal soAmt = BigDecimal.ZERO;

    @Column(name="allocated_amt", precision=18, scale=2)
    private BigDecimal allocatedAmt = BigDecimal.ZERO;

    @Column(name="bo_amt", precision=18, scale=2)
    private BigDecimal boAmt = BigDecimal.ZERO;

    @Column(name="cancel_amt", precision=18, scale=2)
    private BigDecimal cancelAmt = BigDecimal.ZERO;

    @Column(name="shipment_amt", precision=18, scale=2)
    private BigDecimal shipmentAmt = BigDecimal.ZERO;

    @Column(name="shipment_cost", precision=18, scale=4)
    private BigDecimal shipmentCost = BigDecimal.ZERO;

    @Column(name="return_amt", precision=18, scale=2)
    private BigDecimal returnAmt = BigDecimal.ZERO;

    @Column(name="return_cost", precision=18, scale=4)
    private BigDecimal returnCost  = BigDecimal.ZERO;

    @Column(name="receive_amt", precision=18, scale=2)
    private BigDecimal receiveAmt = BigDecimal.ZERO;

    @Column(name="po_amt", precision=18, scale=2)
    private BigDecimal poAmt = BigDecimal.ZERO;

    @Column(name="po_eo_amt", precision=18, scale=2)
    private BigDecimal poEoAmt = BigDecimal.ZERO;

    @Column(name="po_ro_amt", precision=18, scale=2)
    private BigDecimal poRoAmt = BigDecimal.ZERO;

    @Column(name="po_ho_amt", precision=18, scale=2)
    private BigDecimal poHoAmt = BigDecimal.ZERO;

    @Column(name="po_wo_amt", precision=18, scale=2)
    private BigDecimal poWoAmt = BigDecimal.ZERO;

    @Column(name="po_cancel_amt", precision=18, scale=2)
    private BigDecimal poCancelAmt = BigDecimal.ZERO;

    @Column(name="create_datetime", length=14)
    private String createDatetime;
}
