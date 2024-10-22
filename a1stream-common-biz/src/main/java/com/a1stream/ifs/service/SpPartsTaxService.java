package com.a1stream.ifs.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.a1stream.domain.entity.ProductTax;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.ProductTaxRepository;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SpPartsTaxService {

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private ProductTaxRepository productTaxRepository;

    public List<MstProductVO> getMstProductVOList(Set<String> productCdSet, String productClassification) {

        return BeanMapUtils.mapListTo(mstProductRepository.findByProductCdInAndProductClassification(productCdSet, productClassification), MstProductVO.class);
    }

    public List<ProductTaxVO> getProductTaxVOList(Set<Long> productIdSet, String productClassification) {

        return BeanMapUtils.mapListTo(productTaxRepository.findByProductIdInAndProductClassification(productIdSet, productClassification), ProductTaxVO.class);
    }

    public void doPartsTax(List<ProductTaxVO> productTaxVOList,List<ProductTaxVO> productTaxVORemoveList) {

        productTaxRepository.saveInBatch(BeanMapUtils.mapListTo(productTaxVOList, ProductTax.class));
        if(!productTaxVORemoveList.isEmpty()){
            productTaxRepository.deleteAllInBatch(BeanMapUtils.mapListTo(productTaxVORemoveList, ProductTax.class));
        }
    }
}
