package com.a1stream.ifs.service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.entity.CmmMessage;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.repository.CmmMessageRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.SysRoleRepository;
import com.a1stream.domain.vo.CmmMessageVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SpCreditService {

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    @Resource
    private CmmMessageRepository cmmMessageRepository;

    @Resource
    private SysRoleRepository sysRoleRepository;

    public List<CmmSiteMaster> findSiteMasterBySiteIdIn(Set<String> siteIdSet) {
        
        return cmmSiteMasterRepository.findBySiteCdInAndActiveFlag(siteIdSet, CommonConstants.CHAR_Y);
    }

    public void deleteIlliegalData(){

        List<CmmMessage> messageList = cmmMessageRepository.findBySiteIdNotIn(CommonConstants.CHAR_DEFAULT_SITE_ID);
        cmmMessageRepository.deleteAll(messageList);
    }

    public List<Long> getRolIds() {

        return sysRoleRepository.getIdByRoleCdIn(Arrays.asList(PJConstants.RoleCode.SPAREPART
                                                             , PJConstants.RoleCode.SPAREPARTL
                                                             , PJConstants.RoleCode.OWNER
                                                             , PJConstants.RoleCode.MANAGER
                                                             , PJConstants.RoleCode.ACCOUNT));
    }

    public void insertMessage(CmmMessageVO cmmMessage) {
        cmmMessageRepository.save(BeanMapUtils.mapTo(cmmMessage, CmmMessage.class));
    }
}
