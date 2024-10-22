package com.a1stream.common.manager;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.RoleType;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class RoleManager {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepo;

    @Resource
    private SysRoleRepository sysRoleRepository;

    public String getRoleTypeByDealerCode(String dealerCode) {

        if(StringUtils.equals(CommonConstants.CHAR_YMSLX_SITE_ID, dealerCode)) {
            return RoleType.SYSSE;
        }

        if (StringUtils.equals(CommonConstants.CHAR_DEFAULT_SITE_ID, dealerCode)) {
            return RoleType.SALESCOMPANY;
        }

        if (StringUtils.equals(CommonConstants.CHAR_YT00_SITE_ID, dealerCode)) {
            return RoleType.SPESHOP;
        }

        CmmSiteMasterVO siteMasterInfo = BeanMapUtils.mapTo(cmmSiteMasterRepo.findById(dealerCode),CmmSiteMasterVO.class);

        if (siteMasterInfo == null ) return CommonConstants.CHAR_BLANK;

        if(StringUtils.equals(CommonConstants.CHAR_Y, siteMasterInfo.getDoFlag())) {
            return RoleType.DOSHOP;
        }

        return RoleType.SHOP3S;
    }

    public Long getRoleIdByDealerCodeAndDealerType(String dealerCode, String dealerType){

        return sysRoleRepository.getIdByRoleCdAndRoleType(dealerCode, dealerType);
    }
}
