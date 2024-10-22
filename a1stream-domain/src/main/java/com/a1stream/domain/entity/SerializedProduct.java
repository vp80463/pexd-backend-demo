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
@Table(name="serialized_product")
@Setter
@Getter
public class SerializedProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="serialized_product_id", unique=true, nullable=false)
    private Long serializedProductId;

    @Column(name="received_date", length=8)
    private String receivedDate;

    @Column(name="manufacturing_date", length=8)
    private String manufacturingDate;

    @Column(name="product_id", nullable=false)
    private Long productId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="location_info_id")
    private Long locationInfoId;

    @Column(name="service_site_id", length=10)
    private String serviceSiteId;

    @Column(name="stu_site_id", length=10)
    private String stuSiteId;

    @Column(name="stu_date", length=8)
    private String stuDate;

    @Column(name="stu_price", precision=18)
    private BigDecimal stuPrice = BigDecimal.ZERO;

    @Column(name="ev_flag", length=1)
    private String evFlag;

    @Column(name="pdi_flag", length=1)
    private String pdiFlag;

    @Column(name="pdi_start_time", length=8)
    private String pdiStartTime;

    @Column(name="pdi_end_time", length=8)
    private String pdiEndTime;

    @Column(name="pdi_finish_date", length=8)
    private String pdiFinishDate;

    @Column(name="pdi_pic_nm", length=180)
    private String pdiPicNm;

    @Column(name="quality_status", length=20)
    private String qualityStatus;

    @Column(name="stock_status", length=20)
    private String stockStatus;

    @Column(name="sales_status", length=20)
    private String salesStatus;

    @Column(name="cmm_serialized_product_id", nullable=false)
    private Long cmmSerializedProductId;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="plate_no", length=40)
    private String plateNo;

    @Column(name="bar_code", length=40)
    private String barCode;

    @Column(name="engine_no", length=40)
    private String engineNo;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;
}
