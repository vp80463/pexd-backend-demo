package com.a1stream.master.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.manager.InventoryManager;
import com.a1stream.domain.bo.master.CMM050101BO;
import com.a1stream.domain.bo.master.CMM050102BasicInfoBO;
import com.a1stream.domain.bo.master.CMM050102PurchaseControlBO;
import com.a1stream.domain.bo.master.CMM050102SalesControlBO;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.ProductInventory;
import com.a1stream.domain.entity.ReorderGuideline;
import com.a1stream.domain.form.master.CMM050101Form;
import com.a1stream.domain.form.master.CMM050102Form;
import com.a1stream.domain.repository.LocationRepository;
import com.a1stream.domain.repository.MstBrandRepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductInventoryRepository;
import com.a1stream.domain.repository.ReorderGuidelineRepository;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstBrandVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Service
public class CMM0501Service {

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    @Resource
    private ProductInventoryRepository productInventoryRepository;

    @Resource
    private ReorderGuidelineRepository reorderGuidelineRepository;

    @Resource
    private MstBrandRepository mstBrandRepository;

    @Resource
    private LocationRepository locationRepository;

    @Resource
    private InventoryManager inventoryManager;

    public List<ProductInventoryVO> findByProductInventoryIdIn(Set<Long> productInventoryIds){
        return  BeanMapUtils.mapListTo(productInventoryRepository.findByProductInventoryIdIn(productInventoryIds), ProductInventoryVO.class);
    }

    public Page<CMM050101BO> findPartsInformationInquiryAndPageList(CMM050101Form model, String siteId) {
        return mstProductRepository.findPartsInformationInquiryAndPageList(model, siteId);
    }

    public List<CMM050101BO> findPartsInformationInquiryList(CMM050101Form model, String siteId) {
        return mstProductRepository.findPartsInformationInquiryList(model, siteId);
    }

    public List<CMM050102PurchaseControlBO> getPurchaseControlGridAddList(CMM050102Form model, String siteId) {
        return mstProductRepository.getPurchaseControlGridAddList(model, siteId);
    }

    public List<CMM050102PurchaseControlBO> getPurchaseControlGridEditList(CMM050102Form model, String siteId) {
        return mstProductRepository.getPurchaseControlGridEditList(model, siteId);
    }

    public CMM050102BasicInfoBO getBasicInfoList(CMM050102Form model, String siteId) {
        return mstProductRepository.getBasicInfoList(model, siteId);
    }

    public CMM050102SalesControlBO getSaleControlList(CMM050102Form model, String siteId) {
        return mstProductRepository.getSaleControlList(model, siteId);
    }

    public MstProductVO findMstProductVO(Long productId) {

        return BeanMapUtils.mapTo(mstProductRepository.findByProductId(productId), MstProductVO.class);
    }

    public void update(MstProductVO mstProductVO, List<ProductInventoryVO> productInventoryList,Set<Long> beforeLocationIds,Set<Long> afterLocationIds,List<ProductInventoryVO> filteredList) {
        mstProductRepository.save(BeanMapUtils.mapTo(mstProductVO, MstProduct.class));
        productInventoryRepository.saveInBatch(BeanMapUtils.mapListTo(productInventoryList, ProductInventory.class));
        if (!ObjectUtils.isEmpty(filteredList)) {
            productInventoryRepository.deleteAll(BeanMapUtils.mapListTo(filteredList, ProductInventory.class));
        }
        inventoryManager.doUpdateLocationMainFlag(beforeLocationIds,afterLocationIds);
    }

    public void updateEditUc(ReorderGuidelineVO reorderGuidelineVO, List<ReorderGuidelineVO> reorderGuidelineVOList, List<ProductInventoryVO> productInventoryList,Set<Long> beforeLocationIds,Set<Long> afterLocationIds,List<ProductInventoryVO> filteredList) {
        if (!ObjectUtils.isEmpty(reorderGuidelineVO)) {
            reorderGuidelineRepository.saveInBatch(BeanMapUtils.mapListTo(reorderGuidelineVOList, ReorderGuideline.class));
        }
        productInventoryRepository.saveInBatch(BeanMapUtils.mapListTo(productInventoryList, ProductInventory.class));
        if (!ObjectUtils.isEmpty(filteredList)) {
            productInventoryRepository.deleteAll(BeanMapUtils.mapListTo(filteredList, ProductInventory.class));
        }
        inventoryManager.doUpdateLocationMainFlag(beforeLocationIds,afterLocationIds);
    }

    public MstProductCategoryVO findByProductCategoryId(Long middleGroupId) {
        return BeanMapUtils.mapTo(mstProductCategoryRepository.findByProductCategoryId(middleGroupId), MstProductCategoryVO.class);
    }

    public List<ProductInventoryVO> findByFacilityIdsAndProductIdAndSiteId(List<Long> facilityIds, Long productId, String siteId) {
        return BeanMapUtils.mapListTo(productInventoryRepository.findByFacilityIdInAndProductIdAndSiteId(facilityIds, productId, siteId), ProductInventoryVO.class);
    }

    public List<ProductInventoryVO> findByFacilityIdsAndProductIdAndSiteIdAndLocationIdIn(List<Long> facilityIds, Long productId, String siteId, List<Long> locationIds) {
        return BeanMapUtils.mapListTo(productInventoryRepository.findByFacilityIdInAndProductIdAndSiteIdAndLocationIdIn(facilityIds, productId, siteId, locationIds), ProductInventoryVO.class);
    }

    public List<ProductInventoryVO> findByFacilityIdsAndProductIdAndSiteIdAndPrimaryFlag(List<Long> facilityIds, Long productId, String siteId, String y) {
        return BeanMapUtils.mapListTo(productInventoryRepository.findByFacilityIdInAndProductIdAndSiteIdAndPrimaryFlag(facilityIds, productId, siteId, y), ProductInventoryVO.class);
    }

    public List<ReorderGuidelineVO> findReorderGuidelineVOList(String siteId, List<Long> facilityIds, Long productId) {

        return BeanMapUtils.mapListTo(reorderGuidelineRepository.findBySiteIdAndFacilityIdInAndProductId(siteId, facilityIds, productId), ReorderGuidelineVO.class);
    }

    public MstProductVO findPartsId(String siteId, String productCd) {
        return BeanMapUtils.mapTo(mstProductRepository.findBySiteIdAndProductCd(siteId, productCd), MstProductVO.class);
    }

    public List<MstProductVO> findPartsId(String siteId, List<String> productCds) {
        return BeanMapUtils.mapListTo(mstProductRepository.findBySiteIdAndProductCdIn(siteId, productCds), MstProductVO.class);
    }

    public MstBrandVO findBybrandCd(String brandCd) {
        return BeanMapUtils.mapTo(mstBrandRepository.findByBrandCd(brandCd), MstBrandVO.class);
    }

    public MstProductVO findFirstByProductCdAndSiteIdIn(String partsCd, List<String> siteIds) {
        return BeanMapUtils.mapTo(mstProductRepository.findFirstByProductCdAndSiteIdIn(partsCd, siteIds), MstProductVO.class);
    }

    public LocationVO findByPointIdAndSiteIdAndMainLocation(Long facilityId, String siteId, String mainLocation) {
        return BeanMapUtils.mapTo(locationRepository.findByFacilityIdAndSiteIdAndLocationCd(facilityId, siteId, mainLocation), LocationVO.class);
    }

    public List<LocationVO> findByLocationIds(Set<Long> locationId){
        return BeanMapUtils.mapListTo(locationRepository.findByLocationIdIn(locationId), LocationVO.class);
    }

}