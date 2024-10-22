package com.a1stream.common.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationVLForm extends BaseVLForm {

    private List<String> locationTypeList;
    private Long pointId;
    private Long workzone;
    private Long binType;
    private String locationCd;
    private String locationType;
    private String mainLocationSign;
    private Long partsId;
}
