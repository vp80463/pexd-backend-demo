package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.common.MenuTreeBO;
import com.a1stream.domain.bo.common.UserBO;
import com.a1stream.domain.bo.master.CMM070503BO;
import com.a1stream.domain.bo.master.CMM070504BO;
import com.a1stream.domain.form.master.CMM070503Form;
import com.a1stream.domain.form.master.CMM070504Form;
import com.a1stream.master.service.CMM0705Service;
import com.ymsl.solid.base.json.JsonUtils;

import jakarta.annotation.Resource;

@Component
public class CMM0705Facade {

    @Resource
    private CMM0705Service cmm0705Service;

    public List<CMM070503BO> findRoleList(String siteId
                                        , CMM070503Form model) {

        return cmm0705Service.findRoleList(siteId
                                         , model);
    }

    public CMM070504BO getRoleAuthInfo(String siteId, CMM070504Form model) {

    	CMM070504BO resultModel = new CMM070504BO();

    	List<UserBO> userList= cmm0705Service.getRoleUserList(siteId, model);

    	String menuJson = cmm0705Service.getMenuJson(model.getRoleId());
        List<MenuTreeBO> menuList = JsonUtils.toList(menuJson, MenuTreeBO.class);

        resultModel.setRoleId(model.getRoleId());
        resultModel.setRoleCd(model.getRoleCd());
        resultModel.setRoleNm(model.getRoleNm());
        resultModel.setUserList(userList);
        resultModel.setMenuList(menuList);

        return resultModel;
    }
}
