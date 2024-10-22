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
@Table(name="api_mc_sales_order")
@Setter
@Getter
public class ApiMcSalesOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="crm_order_id", unique=true, nullable=false)
    private Long crmOrderId;

    @Column(name="crm_order_no", length=20)
    private String crmOrderNo;

    @Column(name="dealer_cd", nullable=false, length=10)
    private String dealerCd;

    @Column(name="point_cd", length=20)
    private String pointCd;

    @Column(name="sales_date", length=8)
    private String salesDate;

    @Column(name="einvoice_flag", length=1)
    private String einvoiceFlag;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="registration_date", length=8)
    private String registrationDate;

    @Column(name="id_no", length=20)
    private String idNo;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="business_nm", length=60)
    private String businessNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="sns", length=20)
    private String sns;

    @Column(name="gender", length=40)
    private String gender;

    @Column(name="date_of_birth", length=8)
    private String dateOfBirth;

    @Column(name="province_cd", length=20)
    private String provinceCd;

    @Column(name="district_cd", length=20)
    private String districtCd;

    @Column(name="email", length=100)
    private String email;

    @Column(name="address", length=256)
    private String address;

    @Column(name="occupation", length=40)
    private String occupation;

    @Column(name="payment_method", length=40)
    private String paymentMethod;

    @Column(name="cus_tax_cd", length=100)
    private String cusTaxCd;

    @Column(name="dealer_type", length=40)
    private String dealerType;

    @Column(name="order_type", length=40)
    private String orderType;

    @Column(name="selling_price", precision=18)
    private BigDecimal sellingPrice = BigDecimal.ZERO;

    @Column(name="tax_rate", precision=18)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="tax_amount", precision=18)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name="discount_amount", precision=18)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name="amount", precision=18)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name="crm_consumer_id", length=20)
    private String crmConsumerId;


}
