package com.a1stream.master.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.entity.ProductOrderResultHistory;
import com.a1stream.domain.entity.ProductOrderResultSummary;
import com.a1stream.domain.form.master.CMM050901Form;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductOrderResultHistoryRepository;
import com.a1stream.domain.repository.ProductOrderResultSummaryRepository;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductOrderResultHistoryVO;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class CMM0509Service {

    @Resource
    MstProductRepository mstProductRepository;

    @Resource
    MstFacilityRepository mstFacilityRepository;

    @Resource
    ProductOrderResultSummaryRepository productOrderResultSummaryRepository;

    @Resource
    ProductOrderResultHistoryRepository productOrderResultHistoryRepository;

    public MstFacilityVO findByFacilityId(Long pointId) {

        return BeanMapUtils.mapTo(mstFacilityRepository.findByFacilityId(pointId), MstFacilityVO.class);
    }

    public MstProductVO findByProductId(Long partsId) {

        return BeanMapUtils.mapTo(mstProductRepository.findByProductId(partsId), MstProductVO.class);
    }

    public List<MstProductVO> findByProductIdIn(Set<Long> partsIds) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductIdIn(partsIds), MstProductVO.class);
    }

    public List<ProductOrderResultSummaryVO> findProductOrderResultSummaryRepositoryInYears( String siteId
                                                                                     , List<String> targetYear
                                                                                     , List<Long> productId
                                                                                     , Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findProductOrderResultSummeryInYears(siteId, targetYear, productId, facilityId), ProductOrderResultSummaryVO.class);
    }

    public List<ProductOrderResultSummaryVO> findProductOrderResultSummaryRepositoryInYearsNoProductId( String siteId
                                                                                           , List<String> targetYear
                                                                                           , Long facilityId) {

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findProductOrderResultSummeryInYearsNoProductId(siteId, targetYear, facilityId), ProductOrderResultSummaryVO.class);
    }

    public List<ProductOrderResultSummaryVO> findSummaryVOByProductId(String siteId, Long productId, Long facilityId){

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findBySiteIdAndProductIdAndFacilityId(siteId, productId, facilityId),ProductOrderResultSummaryVO.class);
    }

    public List<ProductOrderResultSummaryVO> findSummaryVOByProductIds(String siteId,List<Long>productId, Long facilityId){

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findBySiteIdAndProductIdInAndFacilityId(siteId, productId, facilityId),ProductOrderResultSummaryVO.class);
    }

    public PageImpl<ProductOrderResultSummaryVO> searchProductOrderResult(CMM050901Form model,String siteId,List<String> yearList) {

        return  productOrderResultSummaryRepository.searchPartsDemandList(model, siteId, yearList);
    }


    public List<ProductOrderResultSummaryVO> findBySiteIdAndFacilityId(String siteId, Long facilityId){

        return BeanMapUtils.mapListTo(productOrderResultSummaryRepository.findBySiteIdAndFacilityId(siteId, facilityId),ProductOrderResultSummaryVO.class);
    }

    public void deleteProductOrderResultSummaryRepository(List<ProductOrderResultSummaryVO> vo,List<ProductOrderResultHistoryVO> backupVos){

        productOrderResultSummaryRepository.deleteAllInBatch(BeanMapUtils.mapListTo(vo, ProductOrderResultSummary.class));
        productOrderResultHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(backupVos, ProductOrderResultHistory.class));
    }

    public void editProductOrderResultSummaryRepository(List<ProductOrderResultSummaryVO> deleteVo,List<ProductOrderResultSummaryVO> editVo,List<ProductOrderResultHistoryVO> backupVos){

        if (ObjectUtils.isEmpty(deleteVo)){
            productOrderResultSummaryRepository.deleteAll(BeanMapUtils.mapListTo(deleteVo, ProductOrderResultSummary.class));
        }
        productOrderResultSummaryRepository.saveInBatch(BeanMapUtils.mapListTo(editVo, ProductOrderResultSummary.class));
        productOrderResultHistoryRepository.saveInBatch(BeanMapUtils.mapListTo(backupVos, ProductOrderResultHistory.class));
    }

    public List<ProductOrderResultHistoryVO> findProductResultHistory(List<Long> productId, Long facilityId, String siteId){

        return BeanMapUtils.mapListTo(productOrderResultHistoryRepository.findByProductIdInAndFacilityIdAndSiteId(productId,facilityId, siteId),ProductOrderResultHistoryVO.class);
    }
    public List<MstProductVO> findProductByProductNo(List<String> productNo,List<String> siteId){

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndSiteIdIn(productNo, siteId), MstProductVO.class);
    }

}