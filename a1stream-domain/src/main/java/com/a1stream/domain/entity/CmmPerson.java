package com.a1stream.domain.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.a1stream.common.model.BaseEntity;
import com.ymsl.solid.jpa.usertype.StringJsonUserType;
import com.ymsl.solid.jpa.uuid.SnowflakeIdGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name="cmm_person")
@Setter
@Getter
public class CmmPerson extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="person_id", unique=true, nullable=false)
    private Long personId;

    @Column(name="person_cd", nullable=false, length=40)
    private String personCd;

    @Column(name="person_nm", length=200)
    private String personNm;

    @Column(name="first_nm", length=60)
    private String firstNm;

    @Column(name="middle_nm", length=60)
    private String middleNm;

    @Column(name="last_nm", length=60)
    private String lastNm;

    @Column(name="from_date", nullable=false, length=8)
    private String fromDate;

    @Column(name="to_date", nullable=false, length=8)
    private String toDate;

    @Column(name="person_type", length=64)
    private String personType;

    @Column(name="gender_type", length=40)
    private String genderType;

    @Column(name="id_no", length=64)
    private String idNo;

    @Column(name="photo_url", length=500)
    private String photoUrl;

    @Column(name="contact_tel", length=20)
    private String contactTel;

    @Column(name="contact_mail", length=256)
    private String contactMail;

    @Column(name="province_id")
    private Long provinceId;

    @Column(name="province_nm", length=256)
    private String provinceNm;

    @Column(name="city_id")
    private Long cityId;

    @Column(name="city_nm", length=256)
    private String cityNm;

    @Column(name="address1", length=256)
    private String address1;

    @Column(name="address2", length=256)
    private String address2;

    @Column(name="post_code", length=10)
    private String postCode;

    @Column(name="electronic_signature_url", length=64)
    private String electronicSignatureUrl;

    @Column(name="extend_list")
    @Type(StringJsonUserType.class)
    private String extendList;

    @Column(name="user_id", length=64)
    private String userId;

    @Column(name="fax_no", length=20)
    private String faxNo;

    @Column(name="person_retrieve", length=600)
    private String personRetrieve;
}
