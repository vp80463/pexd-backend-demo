package com.a1stream.domain.entity;

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


@Entity
@Table(name="service_order_job")
@Setter
@Getter
public class ServiceOrderJob extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_order_job_id", unique=true, nullable=false)
    private Long serviceOrderJobId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="seq_no")
    private Integer seqNo = CommonConstants.INTEGER_ZERO;

    @Column(name="service_category_id", length=40)
    private String serviceCategoryId;

    @Column(name="settle_type_id", length=40)
    private String settleTypeId;

    @Column(name="service_demand_id")
    private Long serviceDemandId;

    @Column(name="service_demand_content", length=256)
    private String serviceDemandContent;

    @Column(name="service_order_fault_id")
    private Long serviceOrderFaultId;

    @Column(name="symptom_id")
    private Long symptomId;

    @Column(name="job_id")
    private Long jobId;

    @Column(name="job_cd", length=40)
    private String jobCd;

    @Column(name="job_nm", length=256)
    private String jobNm;

    @Column(name="std_manhour", precision=18)
    private BigDecimal stdManhour = BigDecimal.ZERO;

    @Column(name="standard_price", precision=18)
    private BigDecimal standardPrice = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="discount_amt", precision=18)
    private BigDecimal discountAmt = BigDecimal.ZERO;

    @Column(name="discount", precision=18)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name="vat_rate", precision=18)
    private BigDecimal vatRate = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="selling_price_not_vat", precision=18)
    private BigDecimal sellingPriceNotVat = BigDecimal.ZERO;

    @Column(name="service_package_id")
    private Long servicePackageId;

    @Column(name="service_package_cd", length=40)
    private String servicePackageCd;

    @Column(name="service_package_nm", length=256)
    private String servicePackageNm;
}
