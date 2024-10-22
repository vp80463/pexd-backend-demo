package com.a1stream.domain.vo;

import java.io.Serializable;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstProductCategoryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long productCategoryId;

    private Long cmmProductCategoryId;

    private String categoryType;

    private String categoryCd;

    private String categoryNm;

    private String categotyRetrieve;

    private String productClassification;

    private String fromDate;

    private String toDate;

    private String activeFlag;

    private Long parentCategoryId;

    private String parentCategoryCd;

    private String parentCategoryNm;

    private String allParentId;

    private String allPath;

    private String allNm;

    private Serializable categoryProperty;


}
