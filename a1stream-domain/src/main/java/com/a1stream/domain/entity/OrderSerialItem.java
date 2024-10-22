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
@Table(name="order_serial_item")
@Setter
@Getter
public class OrderSerialItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="service_item_id", unique=true, nullable=false)
    private Long serviceItemId;

    @Column(name="service_order_id")
    private Long serviceOrderId;

    @Column(name="service_category_id")
    private Long serviceCategoryId;

    @Column(name="settle_type_id")
    private Long settleTypeId;

    @Column(name="service_damand_id")
    private Long serviceDamandId;

    @Column(name="fault_info_id")
    private Long faultInfoId;

    @Column(name="symptom_id")
    private Long symptomId;

    @Column(name="product_id")
    private Long productId;

    @Column(name="service_job_content", length=256)
    private String serviceJobContent;

    @Column(name="std_manhour", precision=131089, scale=0)
    private BigDecimal stdManhour = BigDecimal.ZERO;

    @Column(name="std_price", precision=18)
    private BigDecimal stdPrice = BigDecimal.ZERO;

    @Column(name="discount_amt", precision=18)
    private BigDecimal discountAmt = BigDecimal.ZERO;

    @Column(name="discount", precision=18)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name="special_price", precision=18)
    private BigDecimal specialPrice = BigDecimal.ZERO;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="product_package_id")
    private Long productPackageId;


}
