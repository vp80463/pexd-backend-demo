package com.a1stream.ifs.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.service.ProductBO;
import com.a1stream.domain.entity.CmmWarrantyModelPart;
import com.a1stream.domain.repository.CmmWarrantyModelPartRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;

@Service
public class SvWarrantyPartsService {

    @Resource
    private CmmWarrantyModelPartRepository cmmWarrantyPartsRepo;

    @Resource
    private MstProductRepository mstProductRepo;

    public void maintainData(List<CmmWarrantyModelPartVO> updWarrantyModelPartList) {

        cmmWarrantyPartsRepo.saveInBatch(BeanMapUtils.mapListTo(updWarrantyModelPartList, CmmWarrantyModelPart.class));
    }

    public void deleteAllWarrantyModelPart() {

        cmmWarrantyPartsRepo.deleteAllWarrantyModelPart();
    }

    public Map<String, ProductBO> getMstProduct(Set<String> productCds, String productCls) {

        List<ProductBO> result = mstProductRepo.findMstProductInfo(productCds, productCls);

        return result.stream().collect(Collectors.toMap(ProductBO::getProductCd, Function.identity()));
    }
}
