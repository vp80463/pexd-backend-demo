package com.a1stream.domain.vo;

import java.io.Serial;

import com.a1stream.common.model.BaseVO;

import lombok.Getter;
import lombok.Setter;


/**
 * @author dong zhen
 */
@Setter
@Getter
public class CmmMessageVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -8064082567183417631L;

    private Long cmmMessage;

    private String message;

    private String roleType;

    private String sysRoleIdList;
}
