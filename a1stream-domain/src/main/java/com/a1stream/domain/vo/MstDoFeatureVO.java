package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MstDoFeatureVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private Long doFeatureId;

    private String doAccount;

    private String doAcpass;

    private String doArea;

    private String doConvert;

    private String doPatternsd;

    private String doPatternspsv;

    private String doSerialsd;

    private String doSerialspsv;

    private String doUsername;

    private String doUserpass;


}
