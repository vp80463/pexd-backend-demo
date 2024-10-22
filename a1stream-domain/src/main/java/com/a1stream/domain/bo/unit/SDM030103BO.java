package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM030103BO implements Serializable {

    private static final long serialVersionUID = 1L;

    //明细部内容
    private Long entryFacilityId;
    private String entryFacality;
    private String deliveryPoint;
    private String consignee;
    private String dealer;
    private String orderNo;
    private Long dealerId;
    private Long consigneeId;
    private String orderDate;

    //打印
    private String date;

    //条件部内容
    private List<SDM030103DetailBO> detailList = new ArrayList<>();

}
