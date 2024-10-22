package com.a1stream.domain.bo.common;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String userCode;

    private String nickName;
}