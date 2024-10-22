package com.a1stream.common.model;

import lombok.Data;

@Data
public class FileExcuteRequestModel {

    private String siteId;

    private String userId;

    private String screenId;

    private String screenName;

    private String webServerRoot;

    private String excuteSql;

    private String param;

    private String existCheckFlag;

    private String updateProgram;
}
