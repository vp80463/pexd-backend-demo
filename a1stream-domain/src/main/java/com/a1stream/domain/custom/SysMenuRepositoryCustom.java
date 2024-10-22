package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.domain.bo.common.MenuBO;

public interface SysMenuRepositoryCustom {

    String findMenuJson();

    List<MenuBO> findMenuListByUserId(String userId);

    List<MenuBO> findMenuListBySwitchDealer(String userId, String switchRoleType);
}
