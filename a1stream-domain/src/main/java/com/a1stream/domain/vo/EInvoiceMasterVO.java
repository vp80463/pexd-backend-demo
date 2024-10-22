package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EInvoiceMasterVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long dbId;

    private String account;

    private String acpass;

    private String area;

    private String convert;

    private String patternsd;

    private String patternspsv;

    private String serialsd;

    private String serialspsv;

    private String userName;

    private String userpass;
}
