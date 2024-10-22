package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SysUserAuthorityVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private String authorityId;

    private String userId;

    private String roleList;
}
