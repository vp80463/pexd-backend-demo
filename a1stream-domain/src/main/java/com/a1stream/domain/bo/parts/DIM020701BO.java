package com.a1stream.domain.bo.parts;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DIM020701BO extends BaseBO{

    private static final long serialVersionUID = 1L;

    private String dealerCd;

    private Long pointId;

    private String pointCd;

    private Long locationId;

    private String locationCd;

    private String locationType;

    private String locationTypeId;

    private Long binTypeId;

    private String binType;

    private Long workzoneId;

    private String workzoneCd;

    private Long productId;

    private String partsNo;

    private Long productInventorytId;

    private Integer seq;

    private String errorMessage;

    private String warningMessage;

    transient List<String> error;

    transient List<Object[]> errorParam;

    transient List<String> warning;

    transient List<Object[]> warningParam;
}
