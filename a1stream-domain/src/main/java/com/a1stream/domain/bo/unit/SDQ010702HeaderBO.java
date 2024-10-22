package com.a1stream.domain.bo.unit;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SDQ010702HeaderBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String pointCd;
    private String pointNm;
    private Long pointId;
    private String manifestStatus;
    private String manifestStatusDisplay;
    private String supplierCd;
    private String supplierNm;
    private String deliveryNoteNo;
    private String deliveryDate;

}
