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
@Table(name="cmm_price_list")
@Setter
@Getter
public class CmmPriceList extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SNOW_UUID")
    @GenericGenerator(name = "SNOW_UUID", type = SnowflakeIdGenerator.class)
    @Column(name="price_list_id", unique=true, nullable=false)
    private Long priceListId;

    @Column(name="list_no", length=40)
    private String listNo;

    @Column(name="from_date", length=8)
    private String fromDate;

    @Column(name="to_date", length=8)
    private String toDate;

    @Column(name="description", length=256)
    private String description;

    @Column(name="version_no", length=256)
    private String versionNo;

    @Column(name="order_category_id", length=20)
    private String orderCategoryId;

    @Column(name="price_category_id", length=40)
    private String priceCategoryId;

    @Column(name="price_status_type_id", length=40)
    private String priceStatusTypeId;

    @Column(name="product_classification_id", length=40)
    private String productClassificationId;


}
