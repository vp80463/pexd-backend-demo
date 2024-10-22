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
@Table(name="mst_organization")
@Setter
@Getter
public class MstOrganization extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="organization_id", unique=true, nullable=false)
    private Long organizationId;

    @Column(name="cmm_organization_id")
    private Long cmmOrganizationId;

    @Column(name="organization_cd", nullable=false, length=40)
    private String organizationCd;

    @Column(name="organization_nm", nullable=false, length=256)
    private String organizationNm;

    @Column(name="organization_abbr", length=256)
    private String organizationAbbr;

    @Column(name="organization_retrieve", length=600)
    private String organizationRetrieve;

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

    @Column(name="business_type", length=40)
    private String businessType;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="pic_id", length=40)
    private String picId;

    @Column(name="delete_flag", length=1)
    private String deleteFlag;

    @Column(name="contact_tel", length=256)
    private String contactTel;

    @Column(name="contact_fax", length=256)
    private String contactFax;

    @Column(name="contact_pic", length=256)
    private String contactPic;

    @Column(name="contact_mail", length=256)
    private String contactMail;

}
