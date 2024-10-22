package com.a1stream.domain.vo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CmmSiteMasterVO extends BatteryVO {

    private static final long serialVersionUID = 1L;

    private String siteId;

    private String siteCd;

    private String siteNm;

    private String webServerRoot;

    private String siteTypeId;

    private String activeFlag;

    private String doFlag;

    private String localDataSource;

    private String apServerFlag;

    private String updateProgram;

    private Integer updateCount;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;

    private String ymvnFlag;
}
