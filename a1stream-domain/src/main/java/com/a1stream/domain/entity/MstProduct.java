package com.a1stream.domain.entity;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import com.a1stream.common.constants.CommonConstants;
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
@Table(name="mst_product")
@Setter
@Getter
public class MstProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="product_id", unique=true, nullable=false)
    private Long productId;

    @Column(name="product_classification", nullable=false, length=40)
    private String productClassification;

    @Column(name="product_category_id")
    private Long productCategoryId;

    @Column(name="product_cd", nullable=false, length=40)
    private String productCd;

    @Column(name="all_parent_id", length=600)
    private String allParentId;

    @Column(name="all_path", length=600)
    private String allPath;

    @Column(name="all_nm", length=600)
    private String allNm;

    @Column(name="registration_date", length=8)
    private String registrationDate;

    @Column(name="english_description", length=200)
    private String englishDescription;

    @Column(name="local_description", length=200)
    private String localDescription;

    @Column(name="sales_description", length=200)
    private String salesDescription;

    @Column(name="product_property")
    @Type(StringJsonUserType.class)
    private String productProperty;

    @Column(name="product_retrieve", length=600)
    private String productRetrieve;

    @Column(name="std_retail_price", precision=18)
    private BigDecimal stdRetailPrice = BigDecimal.ZERO;

    @Column(name="std_ws_price", precision=18)
    private BigDecimal stdWsPrice = BigDecimal.ZERO;

    @Column(name="std_price_update_date", length=8)
    private String stdPriceUpdateDate;

    @Column(name="sal_lot_size", precision=18)
    private BigDecimal salLotSize = BigDecimal.ZERO;

    @Column(name="pur_lot_size", precision=18)
    private BigDecimal purLotSize = BigDecimal.ZERO;

    @Column(name="min_sal_qty", precision=18)
    private BigDecimal minSalQty = BigDecimal.ZERO;

    @Column(name="min_pur_qty", precision=18)
    private BigDecimal minPurQty = BigDecimal.ZERO;

    @Column(name="sales_status_type", length=1)
    private String salesStatusType;

    @Column(name="brand_id")
    private Long brandId;

    @Column(name="brand_cd", length=40)
    private String brandCd;

    @Column(name="image_url", length=500)
    private String imageUrl;

    @Column(name="battery_flag", length=1)
    private String batteryFlag;

    @Column(name="do_description", length=200)
    private String doDescription;

    @Column(name="color_cd", length=40)
    private String colorCd;

    @Column(name="color_nm", length=200)
    private String colorNm;

    @Column(name="displacement")
    private Integer displacement = CommonConstants.INTEGER_ZERO;

    @Column(name="product_level")
    private Integer productLevel = CommonConstants.INTEGER_ZERO;

    @Column(name="to_product_id")
    private Long toProductId;

    @Column(name="model_type", length=40)
    private String modelType;

    @Column(name="expired_date", length=8)
    private String expiredDate;
}
