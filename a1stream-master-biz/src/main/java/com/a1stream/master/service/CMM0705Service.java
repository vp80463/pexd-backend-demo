package com.a1stream.master.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.manager.RoleManager;
import com.a1stream.domain.bo.common.UserBO;
import com.a1stream.domain.bo.master.CMM070503BO;
import com.a1stream.domain.form.master.CMM070503Form;
import com.a1stream.domain.form.master.CMM070504Form;
import com.a1stream.domain.repository.SysRoleRepository;

import jakarta.annotation.Resource;

@Service
public class CMM0705Service {

    @Resource
    private SysRoleRepository sysRoleRepo;

    @Resource
    private RoleManager roleManager;

    public List<CMM070503BO> findRoleList(String siteId
                                        , CMM070503Form model) {

        String roleType = roleManager.getRoleTypeByDealerCode(siteId);

        return sysRoleRepo.findByRoleAndUser(siteId
                                           , model.getRoleId()
                                           , model.getUserSearch()
                                           , roleType);
    }

    public List<UserBO> getRoleUserList(String siteId
                                      , CMM070504Form model) {

        return sysRoleRepo.findUserListByRoleId(siteId
                                              , model.getRoleId()) ;
    }


    public String getMenuJson(String roleId) {

        return sysRoleRepo.findMenuJsonByRole(roleId);
    }
}
