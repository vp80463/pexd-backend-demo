package com.a1stream.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseVO implements Serializable {

    private static final long serialVersionUID = 2478529332493818462L;

    private String siteId;

    private String toSiteId;

    private String toOrganizationCd;

    private String fromSiteId;

    private String fromOrganizationCd;

    private String updateProgram;

    private Integer updateCount = 0;

    private Integer updateCounter;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;
}