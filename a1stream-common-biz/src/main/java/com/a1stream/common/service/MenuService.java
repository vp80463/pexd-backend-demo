package com.a1stream.common.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.UserHabitConstant;
import com.a1stream.common.manager.RoleManager;
import com.a1stream.domain.bo.common.MenuBO;
import com.a1stream.domain.repository.SysMenuRepository;
import com.a1stream.domain.repository.UserHabitRepository;
import com.a1stream.domain.vo.UserHabitVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * 主页菜单获取逻辑
 *
 * @author mid1341
 */
@Service
public class MenuService {

    @Resource
    SysMenuRepository sysMenuRepo;
    @Resource
    UserHabitRepository userHabitRepo;
    @Resource
    RoleManager roleMgr;

    public List<MenuBO> findMenuListByUserId(String userId){

        return sysMenuRepo.findMenuListByUserId(userId);
    }

    public List<MenuBO> findMenuListBySwitchDealer(String userId, String switchDealer){

        return sysMenuRepo.findMenuListBySwitchDealer(userId, roleMgr.getRoleTypeByDealerCode(switchDealer));
    }

    public UserHabitVO findSwitchDealer(String userId) {
        return BeanMapUtils.mapTo(userHabitRepo.findByUserIdAndSiteIdAndUserHabitTypeId(userId, CommonConstants.CHAR_DEFAULT_SITE_ID, UserHabitConstant.ISCHANGEDEALER), UserHabitVO.class);
    }
}
