package com.a1stream.domain.bo.batch;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SvServiceHistoryIFBO implements Serializable{

    private static final long serialVersionUID = 1L;

    private String queueId;
    private String ServiceHistoryId;
    private String SiteId;
    private String SiteOrderId;
    private String ServiceDate;
    private String OrderNo;
    private String CmmProductId;
    private String SiteProductId;
    private String CmmSerializedProductId;
    private String SiteSerializedProductId;
    private String NoPlate;
    private String FrameNo;
    private String CmmConsumerId;
    private String SiteConsumerId;
    private String ServiceCategoryId;
    private String ServiceCategoryContent;
    private String ServiceDemandContent;
    private String ServiceDemandId;
    private String ServiceSubjectContent;
    private String UpdateAuthor;
    private String UpdateDate;
    private String CreateAuthor;
    private String CreateDate;
    private String UpdateProgram;
    private String UpdateCounter;
    private String Mileage;
    private String FacilityCode;
    private String FacilityName;
    private String OperationStartDate;
    private String OperationStartTime;
    private String OperationFinishDate;
    private String OperationFinishTime;
    private String MechanicPic;
    private String TotalServiceAmount;
    private String JobAmount;
    private String PartsAmount;
    private String CommentForCustomer;
    private String CustomerComment;
}
