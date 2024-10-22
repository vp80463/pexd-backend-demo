package com.a1stream.domain.vo;

import java.util.Date;

import com.a1stream.common.model.BaseVO;
import com.ymsl.solid.base.util.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysUserVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String userCode;

    private String personId;

    private String nickName;

    private String type;

    private String mail;

    private String phone;

    private Date effectiveDate;

    private Date expiredDate;

    private String password;

    public static SysUserVO create() {
        SysUserVO entity = new SysUserVO();
        entity.setUserId(IdUtils.getSnowflakeIdWorker().nextIdStr());

        return entity;
    }
}
