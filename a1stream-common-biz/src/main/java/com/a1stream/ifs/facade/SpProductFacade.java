package com.a1stream.ifs.facade;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.PartsCategory;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductRelationClass;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductRelationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SpProductBO;
import com.a1stream.ifs.service.SpProductImportService;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Component
public class SpProductFacade {

    @Resource
    private SpProductImportService spProductImportService;

    public void importPartsProductInfo(List<SpProductBO> mstPartsProductList) {

        if(mstPartsProductList.isEmpty()) {
            return;
        }

        Map<Long,MstProductRelationVO> productRelationAllVOMap = new HashMap<>();

        //get所有中分类的集合
        Set<String> productCategorys = mstPartsProductList.stream().map(SpProductBO::getProductCategory).collect(Collectors.toSet());
        Map<String, String> partsCategoryMap = mstPartsProductList.stream().collect(Collectors.toMap(SpProductBO::getPartNo, SpProductBO::getProductCategory));

        //查询出所有已存在的productCategory
        List<MstProductCategoryVO> productCategoryVOList = spProductImportService.getProductCategoryVOListBSerialCd(ProductClsType.PART.getCodeDbid(), CommonConstants.CHAR_Y, PartsCategory.MIDDLEGROUP, productCategorys);
        Map<String,MstProductCategoryVO> productCategoryVOMap = productCategoryVOList.stream().collect(Collectors.toMap(MstProductCategoryVO::getCategoryCd, t->t));

        //get所有部品的集合
        List<String> partsNoList = mstPartsProductList.stream().map(SpProductBO :: getPartNo).collect(Collectors.toList());
        List<String> toPartsNoList = mstPartsProductList.stream().map(SpProductBO :: getPartNo).collect(Collectors.toList());

        List<SpProductBO> fromMstPartsProductBOList = mstPartsProductList.stream().filter(v->StringUtils.isNotBlankText(v.getSupersedingPart())).collect(Collectors.toList());

        partsNoList.addAll(fromMstPartsProductBOList.stream().map(SpProductBO :: getSupersedingPart).collect(Collectors.toList()));
        //查询出所有已存在的cmmproductVO
        Map<String, MstProductVO> productVOMap = new HashMap<>();
        List<MstProductVO> productVOList = spProductImportService.getPartsMstProductVOByCodeIn(partsNoList);
        if(CollectionUtils.isNotEmpty(productVOList)) {

            productVOMap = productVOList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, t->t));
        }

        List<MstProductVO> toMstProductVOList = productVOList.stream().filter(v -> toPartsNoList.contains(v.getProductCd())).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(toMstProductVOList)) {
            Set<Long> toProductIdSet = productVOList.stream().map(MstProductVO :: getProductId).collect(Collectors.toSet());

            //查询出所有已存在的替代关系（superSeding）
            List<MstProductRelationVO> productRelationAllVOList = spProductImportService.getPartsMstProductRelationVOByToProdRelationClass(toProductIdSet, ProductRelationClass.SUPERSEDING);
            if(CollectionUtils.isNotEmpty(productRelationAllVOList)) {

                productRelationAllVOMap = productRelationAllVOList.stream().collect(Collectors.toMap(MstProductRelationVO::getToProductId, t->t));
            }
        }

        Set<SpProductBO> insertSet = new HashSet<SpProductBO>();
        Set<SpProductBO> updateSet = new HashSet<SpProductBO>();

        //productRelation  largeCategory
        //将本次productlist分为新增和变更两组进行处理
        for(SpProductBO mstPartsProductBO : mstPartsProductList) {

            mstPartsProductBO.setSupersedingPart(mstPartsProductBO.getSupersedingPart().trim());
            if(productCategoryVOMap.containsKey(mstPartsProductBO.getProductCategory())) {

                if(productVOMap.containsKey(mstPartsProductBO.getPartNo())) {
                    updateSet.add(mstPartsProductBO);
                } else {
                    insertSet.add(mstPartsProductBO);
                }
            }
        }

        //更新mstProduct
        spProductImportService.savePartsAndToLocal(insertSet
                                                  , updateSet
                                                  , productVOMap
                                                  , productCategoryVOMap
                                                  , productRelationAllVOMap
                                                  , partsCategoryMap);
    }

}
