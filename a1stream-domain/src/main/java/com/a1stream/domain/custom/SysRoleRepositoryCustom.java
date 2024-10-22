package com.a1stream.domain.custom;

import java.util.List;

import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.domain.bo.common.RoleBO;
import com.a1stream.domain.bo.common.UserBO;
import com.a1stream.domain.bo.master.CMM070503BO;

public interface SysRoleRepositoryCustom {

    ValueListResultBO findUserRoleList(String roleType
                                , BaseVLForm model);
    
    List<RoleBO> getUserRoleList(String roleType, BaseVLForm model);

    public List<CMM070503BO> findByRoleAndUser(String siteId
                                             , String roleId
                                             , String userSearch
                                             , String roleType);

    public List<UserBO> findUserListByRoleId(String siteId
                                           , String roleId);

    public String findMenuJsonByRole(String roleId);
}
