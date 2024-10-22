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
@Table(name="mst_facility")
@Setter
@Getter
public class MstFacility extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="facility_id", unique=true, nullable=false)
    private Long facilityId;

    @Column(name="facility_cd", nullable=false, length=40)
    private String facilityCd;

    @Column(name="facility_nm", nullable=false, length=256)
    private String facilityNm;

    @Column(name="facility_abbr", length=256)
    private String facilityAbbr;

    @Column(name="facility_retrieve", length=600)
    private String facilityRetrieve;

    @Column(name="numbering_id_cd", length=4)
    private String numberingIdCd;

    @Column(name="sp_purchase_flag", length=1)
    private String spPurchaseFlag;

    @Column(name="facility_role_type", length=40)
    private String facilityRoleType;

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

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="pic_id", length=40)
    private String picId;

    @Column(name="delete_flag", length=1)
    private String deleteFlag;

    @Column(name="do_flag", length=1)
    private String doFlag;

    @Column(name="new_flag", length=1)
    private String newFlag;

    @Column(name="multi_address_flag", length=1)
    private String multiAddressFlag;

    @Column(name="contact_tel", length=256)
    private String contactTel;

    @Column(name="contact_fax", length=256)
    private String contactFax;

    @Column(name="contact_pic", length=256)
    private String contactPic;

    @Column(name="contact_mail", length=256)
    private String contactMail;

    @Column(name="shop_flag", length=1)
    private String shopFlag;

    @Column(name="warehouse_flag", length=1)
    private String warehouseFlag;
}
