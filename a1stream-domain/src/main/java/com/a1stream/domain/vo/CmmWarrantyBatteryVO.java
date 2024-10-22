package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class CmmWarrantyBatteryVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long warrantyBatteryId;

    private Long productId;

    private Long batteryId;

    private String warrantyProductClassification;

    private String fromDate;

    private String toDate;

    private String warrantyProductUsage;

    private String comment;


}
