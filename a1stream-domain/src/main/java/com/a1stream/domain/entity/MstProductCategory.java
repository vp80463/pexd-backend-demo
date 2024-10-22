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
@Table(name="mst_product_category")
@Setter
@Getter
public class MstProductCategory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_category_id", unique=true, nullable=false)
    private Long productCategoryId;

    // @Column(name="cmm_product_category_id")
    // private Long cmmProductCategoryId;

    @Column(name="category_type", length=40)
    private String categoryType;

    @Column(name="category_cd", length=40)
    private String categoryCd;

    @Column(name="category_nm", length=200)
    private String categoryNm;

    @Column(name="categoty_retrieve", length=256)
    private String categotyRetrieve;

    @Column(name="product_classification", nullable=false, length=40)
    private String productClassification;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="active_flag", length=1)
    private String activeFlag;

    @Column(name="parent_category_id")
    private Long parentCategoryId;

    @Column(name="parent_category_cd", length=40)
    private String parentCategoryCd;

    @Column(name="parent_category_nm", length=200)
    private String parentCategoryNm;

    @Column(name="all_parent_id", length=600)
    private String allParentId;

    @Column(name="all_path", length=600)
    private String allPath;

    @Column(name="all_nm", length=600)
    private String allNm;

    @Column(name="category_property")
    @Type(StringJsonUserType.class)
    private String categoryProperty;


}
