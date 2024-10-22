package com.a1stream.unit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM050601BO;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.a1stream.domain.form.unit.SDM050601Form;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Update Period Maintenance
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/28   Wang Nan      New
*/
@Service
public class SDM0506Service {

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepo;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepo;

    public List<SDM050601BO> getUpdPeriodMaintList(SDM050601Form form) {
        return cmmUnitPromotionListRepo.getUpdPeriodMaintList(form);
    }

    public SysUserAuthorityVO findSysUserAuthority(String userId) {
        return BeanMapUtils.mapTo(sysUserAuthorityRepo.findUserBySDAndAccount(userId), SysUserAuthorityVO.class);
    }

    public List<CmmUnitPromotionListVO> getCmmUnitPromotionListVOList(List<Long> promotionListIds) {
        return BeanMapUtils.mapListTo(cmmUnitPromotionListRepo.findByPromotionListIdIn(promotionListIds), CmmUnitPromotionListVO.class);
    }

    public void updateCmmUnitPromotionList(List<CmmUnitPromotionListVO> cmmUnitPromotionListVO) {
        cmmUnitPromotionListRepo.saveInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionListVO, CmmUnitPromotionList.class));
    }

}
