package com.a1stream.parts.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.entity.ReorderGuideline;
import com.a1stream.domain.form.parts.SPM040201Form;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.PartsRopqMonthlyRepository;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.ReorderGuidelineRepository;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PartsRopqMonthlyVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM0402Service {

    @Resource
    private ReorderGuidelineRepository reorderGuidelineRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private PartsRopqMonthlyRepository partsRopqMonthlyRepository;

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    public Page<SPM040201BO> searchRoqRopList(SPM040201Form model,String siteId){

        return reorderGuidelineRepository.searchProductROPROQInfo(model, siteId);
    }

    public List<PartsRopqMonthlyVO> getJ1TotalAndJ2Total(String siteId,Set<Long> productId, Set<String> ropqType){

        return BeanMapUtils.mapListTo(partsRopqMonthlyRepository.findBySiteIdAndProductIdInAndRopqTypeIn(siteId, productId, ropqType), PartsRopqMonthlyVO.class);
    }

    public List<ProductStockStatusVO> findStockStatusList( String siteId
                                              , Long pointId
                                              , Set<Long> productIds
                                              , String productClassification
                                              , Set<String> productStockStatusTypes){

        return BeanMapUtils.mapListTo(productStockStatusRepository.findStockStatusList(siteId, pointId, productIds,productClassification,productStockStatusTypes), ProductStockStatusVO.class);
    }

    public List<ReorderGuidelineVO> findRoqRopByReorderGuidelineId(Set<Long> reorderGuidelineId){

        return BeanMapUtils.mapListTo(reorderGuidelineRepository.findByReorderGuidelineIdIn(reorderGuidelineId), ReorderGuidelineVO.class);
    }

    public ReorderGuidelineVO findRoqRopByReorderGuidelineId(Long reorderGuidelineId){

        return BeanMapUtils.mapTo(reorderGuidelineRepository.findByReorderGuidelineId(reorderGuidelineId), ReorderGuidelineVO.class);
    }

    public List<MstProductVO> findProductByProductNo(List<String> productNo,List<String> siteId){
        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndSiteIdIn(productNo, siteId), MstProductVO.class);
    }

    public List<ReorderGuidelineVO> findReorderGuidelineByProductNo(List<Long> productId,List<String> siteId,Long pointId){
        return BeanMapUtils.mapListTo(reorderGuidelineRepository.findByProductIdInAndSiteIdInAndFacilityId(productId, siteId,pointId), ReorderGuidelineVO.class);
    }

    public void editRoqRopInfo(List<ReorderGuidelineVO> updateList,List<ReorderGuidelineVO> insertList){

        reorderGuidelineRepository.saveInBatch(BeanMapUtils.mapListTo(updateList, ReorderGuideline.class));
        reorderGuidelineRepository.saveInBatch(BeanMapUtils.mapListTo(insertList, ReorderGuideline.class));

    }

    public void deleteRoqRopInfo(ReorderGuidelineVO deleteModel){

        if (!ObjectUtils.isEmpty(deleteModel)) {
            reorderGuidelineRepository.delete(BeanMapUtils.mapTo(deleteModel, ReorderGuideline.class));
        }
    }
}
