package com.a1stream.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class HomePagePointBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1189002027109682503L;
    /**
     * 据点 ID
     */
    private Long pointId;

    /**
     * 据点 CD
     */
    private String pointCd;

    /**
     * 据点 Nm
     */
    private String pointNm;
}