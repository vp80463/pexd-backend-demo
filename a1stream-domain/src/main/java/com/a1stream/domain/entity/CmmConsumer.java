package com.a1stream.domain.entity;

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
@Table(name="cmm_consumer")
@Setter
@Getter
public class CmmConsumer extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="consumer_id", unique=true, nullable=false)
    private Long consumerId;

    @Column(name="consumer_type", length=40)
    private String consumerType;

    @Column(name="vip_no", length=20)
    private String vipNo;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="consumer_full_nm", length=180)
    private String consumerFullNm;

    @Column(name="consumer_retrieve", length=200)
    private String consumerRetrieve;

    @Column(name="gender", length=40)
    private String gender;

    @Column(name="id_no", length=40)
    private String idNo;

    @Column(name="id_classification_no", length=40)
    private String idClassificationNo;

    @Column(name="visa_no", length=40)
    private String visaNo;;

    @Column(name="email", length=100)
    private String email;

    @Column(name="email_2", length=100)
    private String email2;

    @Column(name="telephone", length=20)
    private String telephone;

    @Column(name="fax_no", length=20)
    private String faxNo;

    @Column(name="post_code", length=10)
    private String postCode;

    @Column(name="tax_code", length=40)
    private String taxCode;

    @Column(name="province_geography_id")
    private Long provinceGeographyId;

    @Column(name="city_geography_id")
    private Long cityGeographyId;

    @Column(name="address", length=256)
    private String address;

    @Column(name="address_2", length=256)
    private String address2;

    @Column(name="occupation", length=40)
    private String occupation;

    @Column(name="interest_model", length=256)
    private String interestModel;

    @Column(name="mc_brand", length=40)
    private String mcBrand;

    @Column(name="mc_purchase_date", length=6)
    private String mcPurchaseDate;

    @Column(name="comment", length=256)
    private String comment;

    @Column(name="registration_date", length=8)
    private String registrationDate;

    @Column(name="registration_reason", length=40)
    private String registrationReason;

    @Column(name="sns", length=100)
    private String sns;

    @Column(name="birth_date", length=4)
    private String birthDate;

    @Column(name="birth_year", length=4)
    private String birthYear;

    @Column(name="business_nm", length=180)
    private String businessNm;

    @Column(name="mobile_phone", length=20)
    private String mobilePhone;
}
