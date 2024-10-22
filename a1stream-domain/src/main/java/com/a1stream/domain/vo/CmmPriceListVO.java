package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmPriceListVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long priceListId;

    private String listNo;

    private String fromDate;

    private String toDate;

    private String description;

    private String versionNo;

    private String orderCategoryId;

    private String priceCategoryId;

    private String priceStatusTypeId;

    private String productClassificationId;


}
