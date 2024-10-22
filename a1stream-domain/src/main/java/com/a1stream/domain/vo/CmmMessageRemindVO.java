package com.a1stream.domain.vo;

import com.a1stream.common.model.BaseVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;


/**
 * @author dong zhen
 */
@Setter
@Getter
public class CmmMessageRemindVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -8064082567183417631L;

    private Long messageRemindId;

    private String businessType;

    private String createUser;

    private LocalDateTime createDate;

    private String readType;

    private String readUser;

    private LocalDateTime readDate;

    private String message;

    private String readCategoryType;

    private String roleType;

    private String sysRoleIdList;
}
