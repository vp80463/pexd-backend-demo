package com.a1stream.web.app.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "user info")
public class UserInfoModel {

//    @Schema(description = "user code")
//    private String userCode;
//
//    @Schema(description = "company code")
//    private String siteId;
//
//    @Schema(description = "user name")
//    private String name;

    private String userCode;
    private String userId;
    private String personName;
    private String personCode;
    private Long personId;
    private String loginDateTime;
    private String lastLoginDateTime;
    private String doFlag;

    //666N / dealer
    private String companyCode;
    //dealer
    private String dealerCode;
    private Locale appLocale;

    private String defaultPointCd;
    private Long defaultPointId;
    private String defaultPointNm;

    private String taxPeriod;

    private String localDataSourceId;

    /**
     * 端口
     */
    private String port;

    private Map<String, Serializable> additionalInfo = new HashMap<>();
}
