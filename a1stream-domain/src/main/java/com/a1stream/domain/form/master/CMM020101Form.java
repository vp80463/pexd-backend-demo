package com.a1stream.domain.form.master;

import com.a1stream.common.model.BaseForm;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.domain.bo.master.CMM020101BO;

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
public class CMM020101Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String point;
    private Long pointId;
    private String location;
    private Long locationId;
    private String wz;
    private Long wzId;
    private String locationType;
    private String locationTypeId;
    private String binType;
    private Long binTypeId;
    private String mainLocation;
    private String status;
    private String action;

    private BaseTableData<CMM020101BO> locationData;
}
