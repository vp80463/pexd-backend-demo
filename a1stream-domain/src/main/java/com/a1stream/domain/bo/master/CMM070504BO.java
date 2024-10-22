package com.a1stream.domain.bo.master;

import java.util.List;

import com.a1stream.common.model.BaseBO;
import com.a1stream.domain.bo.common.MenuTreeBO;
import com.a1stream.domain.bo.common.UserBO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CMM070504BO extends BaseBO {

    private static final long serialVersionUID = 1L;

    private String roleCd;
    private String roleNm;
    private String roleId;

    private List<UserBO> userList;
    private List<MenuTreeBO> menuList;
}
