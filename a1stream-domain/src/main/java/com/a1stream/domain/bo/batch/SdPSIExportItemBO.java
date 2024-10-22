package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SdPSIExportItemBO {

    private String date;
    private String point;
    private String model;
    private Integer initialInv;
    private Integer ymvnIn;
    private Integer wholesalesIn;
    private Integer transferIn;
    private Integer retailOut;
    private Integer wholesalesOut;
    private Integer transferOut;
    private Integer intransit;

}
