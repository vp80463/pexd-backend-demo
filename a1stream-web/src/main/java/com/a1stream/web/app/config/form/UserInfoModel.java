package com.a1stream.web.app.config.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "user info")
public class UserInfoModel {

    @Schema(description = "user code")
    private String userCode;

    @Schema(description = "company code")
    private String siteId;

    @Schema(description = "user name")
    private String name;
}
