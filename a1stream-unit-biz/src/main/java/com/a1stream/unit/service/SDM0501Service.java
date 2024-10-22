package com.a1stream.unit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.bo.unit.SDM050102BO;
import com.a1stream.domain.entity.CmmUnitPromotionItem;
import com.a1stream.domain.entity.CmmUnitPromotionList;
import com.a1stream.domain.form.unit.SDM050101Form;
import com.a1stream.domain.form.unit.SDM050102Form;
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
public class SDM0501Service {

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

    public List<SDM050101BO> findSetUpPromotionList(SDM050101Form form) {

        return cmmUnitPromotionListRepository.findSetUpPromotionList(form);
    }

    public SysUserAuthorityVO findSysUserAuthority(String userId) {

        return BeanMapUtils.mapTo(sysUserAuthorityRepository.findUserBySDAndAccount(userId), SysUserAuthorityVO.class);
    }

    public List<SDM050101BO> findSetUpPromotionListExport(SDM050101Form form) {

        return cmmUnitPromotionListRepository.findSetUpPromotionListExport(form);
    }

    public SDM050101BO findDetailAndAddFlag(String userId) {

        return sysUserAuthorityRepository.findDetailAndAddFlag(userId);
    }

    public List<SDM050102BO> findPromotionMC(SDM050102Form form) {

        return cmmUnitPromotionListRepository.findPromotionMC(form);
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

    public void doConfirm(List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList, CmmUnitPromotionListVO cmmUnitPromotionListVO) {

        cmmUnitPromotionItemRepository.saveInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionItemVOList, CmmUnitPromotionItem.class));
        cmmUnitPromotionListRepository.save(BeanMapUtils.mapTo(cmmUnitPromotionListVO, CmmUnitPromotionList.class));
    }

    public List<CmmUnitPromotionItemVO> getByframeNosAndPromotionListId(List<String> frameNos, Long promotionListId) {
        return BeanMapUtils.mapListTo(cmmUnitPromotionItemRepository.findByFrameNoInAndPromotionListId(frameNos, promotionListId), CmmUnitPromotionItemVO.class);
    }

    public void doDelete(List<CmmUnitPromotionItemVO> cmmUnitPromotionItemVOList) {
        cmmUnitPromotionItemRepository.deleteAllInBatch(BeanMapUtils.mapListTo(cmmUnitPromotionItemVOList, CmmUnitPromotionItem.class));
    }
}
