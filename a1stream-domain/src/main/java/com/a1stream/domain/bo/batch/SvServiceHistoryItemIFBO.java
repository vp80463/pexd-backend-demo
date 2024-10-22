package com.a1stream.domain.bo.batch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServiceHistoryItemIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String queueId;
    private String ServiceHistoryItemId;
    private String SiteId;
    private String CmmProductId;
    private String SiteProductId;
    private String ProductContent;
    private String ProductClassificationId;
    private String Qty;
    private String StandardPrice;
    private String SellingPrice;
    private String Amount;
    private String ServiceHistoryId;
    private String UpdateAuthor;
    private String UpdateDate;
    private String CreateAuthor;
    private String CreateDate;
    private String UpdateProgram;
    private String UpdateCounter;
    private String ServiceCategoryId;
    private String ServiceDemandId;
}
