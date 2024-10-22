package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseInfResponse implements Serializable {

    private static final long serialVersionUID = 4698578284120266636L;

    //响应码
    private String code;

    //响应信息
    private String message;

    //响应数据
    private String data;
}
