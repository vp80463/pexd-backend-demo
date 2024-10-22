package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationVLBO extends BaseVLBO {

    private static final long serialVersionUID = 1L;

    private String locationNo;

    private String locationType;

    private String locationTypeCd;

    private Long workzoneId;

    private String workzone;

    private String workzoneDescription;

    private String binType;

    private Long binTypeId;

    private String binTypeDescription;

    private String mainLocation;

    private Long locationId;

    private String locationTypeCode;
}
