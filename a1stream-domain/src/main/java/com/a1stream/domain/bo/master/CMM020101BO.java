package com.a1stream.domain.bo.master;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
*
* 功能描述: Location Maintenance
*
* @author mid2215
*/
@Getter
@Setter
public class CMM020101BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String location;
    private Long locationId;
    private Long pointId;
    private String point;
    private String locationType;
    private String locationTypeId;
    private String binType;
    private Long binTypeId;
    private String wz;
    private Long wzId;
    private String siteId;
    private String mainLocation;
}