package com.a1stream.domain.bo.unit;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDM010202ScanBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String frameNo;

    private String engineNo;

    private String qualityStatus;

    private String stockStatus;

    private Long facilityId;

    private Long serializedProductId;

    private Long cmmSerializedProductId;

    private Long productId;

    private String errorMessage;

    transient List<String> error;

    transient List<Object[]> errorParam;
}
