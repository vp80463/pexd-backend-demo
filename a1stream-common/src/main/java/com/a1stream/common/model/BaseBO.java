package com.a1stream.common.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseBO implements Serializable {

    private static final long serialVersionUID = -6152123301833655754L;

    private String siteId;

    private String toSiteId;

    private String fromSiteId;

    private String updateProgram;

    private Integer updateCount;
}