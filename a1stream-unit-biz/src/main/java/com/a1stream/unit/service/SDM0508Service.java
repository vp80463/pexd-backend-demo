package com.a1stream.unit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.entity.CmmUnitPromotionItem;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.domain.repository.CmmPromotionOrderRepository;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmUnitPromotionItemRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.a1stream.domain.vo.CmmPromotionOrderVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.SerializedProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述: Promotion Data Recovery
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/
@Service
public class SDM0508Service {

    @Resource
    private CmmPromotionOrderRepository cmmPromotionOrderRepo;

    @Resource
    private SerializedProductRepository serializedProductRepo;

    @Resource
    private CmmUnitPromotionItemRepository cmmUnitPromotionItemRepo;

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;

    public List<SDM050801BO> getCpoPromoRecList(SDM050801Form form) {
        return cmmPromotionOrderRepo.getCpoPromoRecList(form);
    }

    public List<SDM050801BO> getSpPromoRecList(SDM050801Form form) {
        return serializedProductRepo.getSpPromoRecList(form);
    }

    public List<CmmUnitPromotionItemVO> getCmmUnitPromotionItemVOList(SDM050801Form form) {
        return BeanMapUtils.mapListTo(cmmUnitPromotionItemRepo.findByPromotionListIdAndFacilityIdAndFrameNo(form.getPromotionId(), form.getPointId(), form.getFrameNo()), CmmUnitPromotionItemVO.class);
    }

    public List<SerializedProductVO> getSerializedProductVOList(List<Long> serializedProductIds, String salesStatus) {
        return BeanMapUtils.mapListTo(serializedProductRepo.findBySerializedProductIdInAndSalesStatus(serializedProductIds, salesStatus), SerializedProductVO.class);
    }

    public SDM050801BO getActivePromotionList(SDM050801Form form) {
        return cmmUnitPromotionListRepo.getActivePromotionList(form);
    }

    public SDM050801BO getPromotionProduct(SDM050801Form form) {
        return cmmSerializedProductRepo.getPromotionProduct(form);
    }

    public void save(List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList,
                     List<CmmPromotionOrderVO> cmmPromotionOrderVOList) {

        cmmUnitPromotionItemRepo.saveInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionItemVOList, CmmUnitPromotionItem.class));
        cmmUnitPromotionListRepo.saveInBatch(BeanMapUtils.mapListTo(cmmPromotionOrderVOList, CmmUnitPromotionList.class));
    }
}
