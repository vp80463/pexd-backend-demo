package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseHelperBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codeDbid;

    private String codeId;

    private String key1;

    private String key2;

    private String codeData1;

    private String codeData2;

    private String codeData3;

    public BaseHelperBO() {}

    public BaseHelperBO(String codeDbid, String codeData1) {

        this.codeDbid = codeDbid;
        this.codeData1 = codeData1;
    }
}