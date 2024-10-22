package com.a1stream.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.a1stream.common.constants.CommonConstants;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="cmm_employee_instruction")
@Setter
@Getter
public class CmmEmployeeInstruction  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="cmm_employee_instruction_id", unique=true, nullable=false)
    private Long cmmEmployeeInstructionId;

    @Column(name="site_id",length=10)
    private String siteId;

    @Column(name="customer_id")
    private Long customerId;

    @Column(name="facility_id")
    private Long facilityId;

    @Column(name="order_no", length=20)
    private String orderNo;

    @Column(name="consumer_id")
    private Long consumerId;

    @Column(name="consumer_cd", length=40)
    private String consumerCd;

    @Column(name="consumer_nm", length=180)
    private String consumerNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;

    @Column(name="order_status", length=20)
    private String orderStatus;

    @Column(name="model_cd", length=40)
    private String modelCd;

    @Column(name="model_nm", length=256)
    private String modelNm;

    @Column(name="frame_no", length=40)
    private String frameNo;

    @Column(name="engine_no", length=40)
    private String engineNo;

    @Column(name="warranty_no", length=20)
    private String warrantyNo;

    @Column(name="color_nm", length=40)
    private String colorNm;

    @Column(name="serialized_product_id")
    private Long serializedProductId;

    @Column(name="tax_rate", precision=18)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(name="custax_cd", length=100)
    private String custaxCd;

    @Column(name="model_type", length=40)
    private String modelType;

    @Column(name="displacement")
    private Integer displacement = CommonConstants.INTEGER_ZERO;

    @Column(name="payment_method", length=80)
    private String paymentMethod;

    @Column(name="email", length=100)
    private String email;

    @Column(name="employee_cd", length=20)
    private String employeeCd;

    @Column(name="employee_nm", length=180)
    private String employeeNm;

    @Column(name="order_month", length=6)
    private String orderMonth;

    @Column(name="cmm_product_id")
    private Long cmmProductId;

    @Column(name="local_order_id")
    private Long localOrderId;

    @Column(name="order_date", length=8)
    private String orderDate;

    @Column(name="purchase_type", length=40)
    private String purchaseType;

    @Column(name="employee_discount", precision=18)
    private BigDecimal employeeDiscount = BigDecimal.ZERO;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="sns", length=100)
    private String sns;

    @Column(name="gender", length=40)
    private String gender;

    @Column(name="province_geography_id")
    private Long provinceGeographyId;

    @Column(name="province_geography_nm", length=200)
    private String provinceGeographyNm;

    @Column(name="city_geography_id")
    private Long cityGeographyId;

    @Column(name="city_geography_nm", length=200)
    private String cityGeographyNm;

    @Column(name="address", length=256)
    private String address;

    @Column(name="birth_date", length=4)
    private String birthDate;

    @Column(name="birth_year", length=4)
    private String birthYear;

    @Version
    @Column(name = "update_count")
    private Integer updateCount;

    @LastModifiedBy
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;

    @LastModifiedDate
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @CreatedBy
    @Column(name = "created_by", length=60)
    private String createdBy;

    @CreatedDate
    @Column(name = "date_created", length=60)
    private LocalDateTime dateCreated;

    @Column(name="update_program", length=20)
    private String updateProgram;
}
