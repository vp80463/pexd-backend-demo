package com.a1stream.unit.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM050701BO;
import com.a1stream.domain.entity.CmmPromotionOrder;
import com.a1stream.domain.form.unit.SDM050701Form;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Waiting Screen
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/
@Service
public class SDM0507Service {

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepo;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepo;

    public Page<SDM050701BO> getWaitingScreenList(SDM050701Form form) {
        return cmmPromotionOrderRepo.getWaitingScreenList(form);
    }

    public SysUserAuthorityVO findSysUserAuthority(String userId) {
        return BeanMapUtils.mapTo(sysUserAuthorityRepo.findUserBySDAndAccount(userId), SysUserAuthorityVO.class);
    }

    public List<CmmPromotionOrderVO> getCmmPromotionOrderVOList(List<Long> promotionOrderIds) {
        return BeanMapUtils.mapListTo(cmmPromotionOrderRepo.findByPromotionOrderIdIn(promotionOrderIds), CmmPromotionOrderVO.class);
    }

    public void updateCmmPromotionOrderList(List<CmmPromotionOrderVO> cmmPromotionOrderVOList) {
        cmmPromotionOrderRepo.saveInBatch(BeanMapUtils.mapListTo(cmmPromotionOrderVOList, CmmPromotionOrder.class));
    }

}
