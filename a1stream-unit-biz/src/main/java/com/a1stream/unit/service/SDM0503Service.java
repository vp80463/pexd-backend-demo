package com.a1stream.unit.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM050301BO;
import com.a1stream.domain.form.unit.SDM050301Form;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer check Promotion Result
*
* mid2303
* 2024年8月30日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/30   Ruan Hansheng   New
*/
@Service
public class SDM0503Service {

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    public Page<SDM050301BO> getPromotionResult(SDM050301Form form) {

        return cmmPromotionOrderRepository.getPromotionResult(form);
    }

    public MstFacilityVO getMstFacilityVO(Long facilityId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(facilityId), MstFacilityVO.class);
    }

    public CmmUnitPromotionListVO getCmmUnitPromotionListVO(Long promotionListId) {

        return BeanMapUtils.mapTo(cmmUnitPromotionListRepository.findByPromotionListId(promotionListId), CmmUnitPromotionListVO.class);
    }

    public SerializedProductVO getSerializedProductVO(String siteId, String frameNo) {

        return BeanMapUtils.mapTo(serializedProductRepository.findBySiteIdAndFrameNo(siteId, frameNo), SerializedProductVO.class);
    }
}
