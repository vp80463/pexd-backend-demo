package com.a1stream.unit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ050101BO;
import com.a1stream.domain.form.unit.SDQ050101Form;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SDQ0501Service {

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    public Page<SDQ050101BO> findPromotionMCList(SDQ050101Form form) {

        return cmmUnitPromotionListRepository.findPromotionMCList(form);
    }

    public SysUserAuthorityVO findSysUserAuthority(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findUserBySDAndAccount(userId), SysUserAuthorityVO.class);
    }

    public List<SDQ050101BO> findPromotionMCListExport(SDQ050101Form form) {

        return cmmUnitPromotionListRepository.findPromotionMCListExport(form);
    }
}
