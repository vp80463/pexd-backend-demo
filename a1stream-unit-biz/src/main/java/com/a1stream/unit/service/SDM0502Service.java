package com.a1stream.unit.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.manager.GenerateNoManager;
import com.a1stream.domain.bo.unit.SDM050201BO;
import com.a1stream.domain.bo.unit.SDM050202BO;
import com.a1stream.domain.bo.unit.SDM050202HeaderBO;
import com.a1stream.domain.entity.CmmUnitPromotionItem;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.a1stream.domain.entity.CmmUnitPromotionModel;
import com.a1stream.domain.form.unit.SDM050201Form;
import com.a1stream.domain.form.unit.SDM050202Form;
import com.a1stream.domain.repository.CmmSerializedProductRepository;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.CmmUnitPromotionItemRepository;
import com.a1stream.domain.repository.CmmUnitPromotionListRepository;
import com.a1stream.domain.repository.CmmUnitPromotionModelRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SysUserAuthorityRepository;
import com.a1stream.domain.vo.CmmSerializedProductVO;
import com.a1stream.domain.vo.CmmSiteMasterVO;
import com.a1stream.domain.vo.CmmUnitPromotionItemVO;
import com.a1stream.domain.vo.CmmUnitPromotionListVO;
import com.a1stream.domain.vo.CmmUnitPromotionModelVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.SysUserAuthorityVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
@Service
public class SDM0502Service {

    @Resource
    private CmmUnitPromotionListRepository cmmUnitPromotionListRepository;

    @Resource
    private SysUserAuthorityRepository sysUserAuthorityRepository;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepo;

    @Resource
    private CmmSerializedProductRepository cmmSerializedProductRepo;

    @Resource
    private CmmUnitPromotionModelRepository cmmUnitPromotionModelRepository;

    @Resource
    private CmmUnitPromotionItemRepository cmmUnitPromotionItemRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private GenerateNoManager generateNoManager;

    public List<SDM050201BO> findSetUpPromotionTerms(SDM050201Form form) {

        return cmmUnitPromotionListRepository.findSetUpPromotionTerms(form);
    }

    public SysUserAuthorityVO findSysUserAuthority(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findUserBySDAndAccount(userId), SysUserAuthorityVO.class);
    }

    public List<CmmUnitPromotionItemVO> findItemByPromotionListId(Long promotionListId) {

        return BeanMapUtils.mapListTo(cmmUnitPromotionItemRepository.findByPromotionListId(promotionListId), CmmUnitPromotionItemVO.class);
    }

    public List<CmmUnitPromotionModelVO> findByPromotionListIdAndProductId(Long promotionListId, List<Long> modelIds) {

        return BeanMapUtils.mapListTo(cmmUnitPromotionModelRepository.findByPromotionListIdAndProductIdIn(promotionListId, modelIds), CmmUnitPromotionModelVO.class);
    }

    public List<MstProductVO> getMstProduct(List<Long> modelIds) {
        Set<Long> modelIdSet = new HashSet<>(modelIds);
        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(modelIdSet), MstProductVO.class);
    }

    public void newPromotionCd(CmmUnitPromotionListVO newCmmUnitPromotionListVO) {

        newCmmUnitPromotionListVO.setPromotionCd(generateNoManager.
                generatePromotionCd(newCmmUnitPromotionListVO.getPromotionListId()));
    }

    public void doConfirm(List<CmmUnitPromotionModelVO> deleteVOs, List<CmmUnitPromotionListVO> listAddVOs, List<CmmUnitPromotionModelVO> modelAddVOs) {

        cmmUnitPromotionModelRepository.saveInBatch(BeanMapUtils.mapListTo(modelAddVOs, CmmUnitPromotionModel.class));
        cmmUnitPromotionModelRepository.deleteAllInBatch(BeanMapUtils.mapListTo(deleteVOs, CmmUnitPromotionModel.class));
        cmmUnitPromotionListRepository.saveInBatch(BeanMapUtils.mapListTo(listAddVOs, CmmUnitPromotionList.class));
    }

    public List<SDM050202BO> findUploadPromoQC(SDM050202Form form) {

        return cmmUnitPromotionListRepository.findUploadPromoQC(form);
    }

    public SDM050202HeaderBO findUploadPromoQCHeader(SDM050202Form form) {

        return cmmUnitPromotionListRepository.findUploadPromoQCHeader(form);
    }

    public CmmUnitPromotionListVO getNewUnitPromotionList() {

        return BeanMapUtils.mapTo(cmmUnitPromotionListRepository.findCmmUnitPromotionList(), CmmUnitPromotionListVO.class);
    }

    public CmmUnitPromotionListVO findByPromotionListId(Long promotionListId) {

        return BeanMapUtils.mapTo(cmmUnitPromotionListRepository.findByPromotionListId(promotionListId), CmmUnitPromotionListVO.class);
    }

    public List<CmmSiteMasterVO> getDealerByCd(Set<String> dealerCds) {

        return BeanMapUtils.mapListTo(cmmSiteMasterRepo.findBySiteCdInAndActiveFlag(dealerCds, CommonConstants.CHAR_Y), CmmSiteMasterVO.class);
    }

    public List<MstFacilityVO> getFacilityByCd(Set<String> facilityCds, String siteId) {

        return BeanMapUtils.mapListTo(mstFacilityRepo.findBySiteIdAndFacilityCdIn(siteId, facilityCds), MstFacilityVO.class);
    }

    public List<MstProductVO> getProductByCds(Set<String> productCds, List<String> siteList) {

        return BeanMapUtils.mapListTo(mstProductRepo.getProductByCds(new ArrayList<>(productCds), siteList, ProductClsType.GOODS.getCodeDbid()), MstProductVO.class);
    }

    public List<CmmSerializedProductVO> findByFrameNos(Set<String> frameNos) {

        return BeanMapUtils.mapListTo(cmmSerializedProductRepo.findByFrameNoIn(frameNos), CmmSerializedProductVO.class);
    }

    public List<CmmUnitPromotionModelVO> findFrameNoByPromotionListId(Long promotionListId) {

        return BeanMapUtils.mapListTo(cmmUnitPromotionModelRepository.findByPromotionListId(promotionListId), CmmUnitPromotionModelVO.class);
    }

    public void doConfirmUploadPromoQC(List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList, CmmUnitPromotionListVO cmmUnitPromotionListVO) {

        cmmUnitPromotionItemRepository.saveInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionItemVOList, CmmUnitPromotionItem.class));
        cmmUnitPromotionListRepository.save(BeanMapUtils.mapTo(cmmUnitPromotionListVO, CmmUnitPromotionList.class));
    }
}
