package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SdProductImportService {

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private MstProductCategoryRepository mstProductCategoryRepository;

    public List<MstProductVO> getMstProductVOList(Set<String> productCdSet, String productClassification) {
        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndProductClassification(productCdSet, productClassification), MstProductVO.class);
    }

    public List<MstProductCategoryVO> getMstProductCategoryVOList(Set<String> categoryCdSet, String siteId) {
        return BeanMapUtils.mapListTo(mstProductCategoryRepository.findByCategoryCdInAndSiteId(categoryCdSet, siteId), MstProductCategoryVO.class);
    }

    public void saveMstProductVOList(List<MstProductVO> productVOList) {
        mstProductRepository.saveInBatch(BeanMapUtils.mapListTo(productVOList, MstProduct.class));
    }

}
