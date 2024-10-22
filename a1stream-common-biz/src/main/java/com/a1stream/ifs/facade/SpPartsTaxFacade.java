package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductTaxVO;
import com.a1stream.ifs.bo.SpPartsTaxModelBO;
import com.a1stream.ifs.service.SpPartsTaxService;

import jakarta.annotation.Resource;

@Component
public class SpPartsTaxFacade {

    @Resource
    private SpPartsTaxService spPartsTaxService;

    public void doPartsTax(List<SpPartsTaxModelBO> spPartsTaxModelBOList){

        Set<String> productCdSet = spPartsTaxModelBOList.stream().map(SpPartsTaxModelBO::getPartNo).collect(Collectors.toSet());
        List<MstProductVO> mstProductVOList = spPartsTaxService.getMstProductVOList(productCdSet, ProductClsType.PART.getCodeDbid());
        Map<String, Long> mstProductVOMap = mstProductVOList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        Set<Long> productIdSet = mstProductVOList.stream().map(MstProductVO::getProductId).collect(Collectors.toSet());
        List<ProductTaxVO> productTaxVOList = spPartsTaxService.getProductTaxVOList(productIdSet, ProductClsType.PART.getCodeDbid());
        Map<Long, ProductTaxVO> productTaxVOMap = productTaxVOList.stream().collect(Collectors.toMap(ProductTaxVO::getProductId, productTaxVO -> productTaxVO));

        if (ObjectUtils.isEmpty(productTaxVOList)) {
            productTaxVOList = new ArrayList<>();
        }

        List<ProductTaxVO> productTaxVORemoveList = new ArrayList<>();

        for (SpPartsTaxModelBO bo : spPartsTaxModelBOList) {
            Long productId = mstProductVOMap.get(bo.getPartNo());
            if (null == productId) {
                continue;
            }

            ProductTaxVO productTaxVO = productTaxVOMap.get(productId);
            if (null == productTaxVO && new BigDecimal(CommonConstants.CHAR_EIGHT).compareTo(bo.getVatRate()) == 0) {
                ProductTaxVO newProductTaxVO = new ProductTaxVO();
                newProductTaxVO.setProductId(productId);
                newProductTaxVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                newProductTaxVO.setTaxRate(bo.getVatRate());
                newProductTaxVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
                productTaxVOList.add(newProductTaxVO);
            } else if (null != productTaxVO && new BigDecimal(CommonConstants.CHAR_TEN).compareTo(bo.getVatRate()) == 0) {
                productTaxVOList.remove(productTaxVO);
                productTaxVORemoveList.add(productTaxVO);
            }
        }
        spPartsTaxService.doPartsTax(productTaxVOList,productTaxVORemoveList);
    }
}