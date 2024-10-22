package com.a1stream.ifs.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SdProductImportBO;
import com.a1stream.ifs.service.SdProductImportService;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

@Component
public class SdProductImportFacade {

    @Resource
    private SdProductImportService sdProductImportService;

    /**
     * 取得sdMasterI/F文件, 更新sdMaster
     */
    public void sdProductImport(List<SdProductImportBO> sdProductInfo) {

        if(sdProductInfo == null || sdProductInfo.isEmpty()) {
            return;
        }

        //将modelCode和productCode+colorCode作为key
        Set<String> modelCodeCdSet = sdProductInfo.stream().map(SdProductImportBO::getModelCode).collect(Collectors.toSet());
        Set<String> productCdSet = sdProductInfo.stream().map(bo -> bo.getProductCode() + bo.getColorCode()).collect(Collectors.toSet());
        productCdSet.addAll(modelCodeCdSet);

        //检索mst_product
        List<MstProductVO> mstProductVOList = sdProductImportService.getMstProductVOList(productCdSet, ProductClsType.GOODS.getCodeDbid());
        Map<String, MstProductVO> mstProductVOMap = Optional.ofNullable(mstProductVOList)
                                                            .orElse(Collections.emptyList())
                                                            .stream().collect(Collectors.toMap(MstProductVO::getProductCd,
                                                                                               mstProductVO -> mstProductVO));

        //检索mst_product_category
        Set<String> modelTypeSet = sdProductInfo.stream().map(SdProductImportBO::getModelType).collect(Collectors.toSet());
        List<MstProductCategoryVO> mstProductCategoryVOList = sdProductImportService.getMstProductCategoryVOList(modelTypeSet, CommonConstants.CHAR_DEFAULT_SITE_ID);
        Map<String, MstProductCategoryVO> mstProductCategoryVOMap = Optional.ofNullable(mstProductCategoryVOList)
                                                                            .orElse(Collections.emptyList())
                                                                            .stream().collect(Collectors.toMap(MstProductCategoryVO::getCategoryCd,
                                                                                                               mstProductCategoryVO -> mstProductCategoryVO));

        //创建用于保存和更新的List
        List<MstProductVO> saveMstProductTaxVOList = new ArrayList<>();

        for (SdProductImportBO bo :sdProductInfo) {

            //2.1若modelCode存在则更新, 反之则新增
            this.prepareMstProductByModel(saveMstProductTaxVOList, mstProductVOMap, mstProductCategoryVOMap, bo);

            //2.2productCode+colorCode存在则更新, 反之则新增
            this.prepareMstProductByProduct(saveMstProductTaxVOList, mstProductVOMap, bo);

        }

        sdProductImportService.saveMstProductVOList(mstProductVOList);
    }

    /**
     * 根据modelCode更新或新增mstProduct
     */
    private void prepareMstProductByModel(List<MstProductVO> saveMstProductTaxVOList,
                                          Map<String, MstProductVO> mstProductVOMap,
                                          Map<String, MstProductCategoryVO> mstProductCategoryVOMap,
                                          SdProductImportBO bo) {

        MstProductCategoryVO mstProductCategoryVO = mstProductCategoryVOMap.get(bo.getModelType());

        MstProductVO mstProductVO = mstProductVOMap.getOrDefault(bo.getModelCode(), MstProductVO.create());

        mstProductVO.setExpiredDate(bo.getOutEffectiveDate());
        mstProductVO.setLocalDescription(bo.getModelName());  //File.ModelName
        mstProductVO.setEnglishDescription(bo.getModelName());//File.ModelName
        mstProductVO.setSalesDescription(bo.getModelName());  //File.ModelName
        mstProductVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        mstProductVO.setSalLotSize(BigDecimal.ONE);
        mstProductVO.setPurLotSize(BigDecimal.ONE);
        mstProductVO.setMinSalQty(BigDecimal.ONE);
        mstProductVO.setMinPurQty(BigDecimal.ONE);
        mstProductVO.setProductCd(bo.getModelCode());//File.ModelCode
        mstProductVO.setRegistrationDate(bo.getEffectiveDate());
        mstProductVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        mstProductVO.setStdRetailPrice(bo.getStdRetailPrice());
        mstProductVO.setProductRetrieve(bo.getModelCode() + bo.getModelName());//File.getModelCode()+File.getModelName()

        if (!Nulls.isNull(mstProductCategoryVO)) {

            String allPath = StringUtils.isNotBlank(mstProductCategoryVO.getCategoryCd())
                    ? mstProductCategoryVO.getCategoryCd() + CommonConstants.CHAR_VERTICAL_BAR + bo.getModelCode()
                    : bo.getModelCode();

            String allNm = StringUtils.isNotBlank(mstProductCategoryVO.getCategoryNm())
                    ? mstProductCategoryVO.getCategoryNm() + CommonConstants.CHAR_VERTICAL_BAR + bo.getModelName()
                    : bo.getModelName();

            mstProductVO.setAllPath(allPath);
            mstProductVO.setAllNm(allNm);
            mstProductVO.setAllParentId(mstProductCategoryVO.getProductCategoryId().toString());
            mstProductVO.setProductCategoryId(mstProductCategoryVO.getProductCategoryId());
        }

        mstProductVO.setDisplacement(bo.getDisplacement());
        mstProductVO.setProductLevel(CommonConstants.INTEGER_ZERO);//level:0

        saveMstProductTaxVOList.add(mstProductVO);
    }

    /**
     * 根据productCode+colorCode更新或新增mstProduct
     */
    private void prepareMstProductByProduct(List<MstProductVO> saveMstProductTaxVOList,
                                            Map<String, MstProductVO> mstProductVOMap,
                                            SdProductImportBO bo) {

        MstProductVO mstProductVO = mstProductVOMap.getOrDefault(bo.getProductCode() + bo.getColorCode(), MstProductVO.create());

        mstProductVO.setExpiredDate(bo.getOutEffectiveDate());
        mstProductVO.setLocalDescription(bo.getProductSalesName());  //File.ProductSalesName
        mstProductVO.setEnglishDescription(bo.getProductSalesName());//File.ProductSalesName
        mstProductVO.setSalesDescription(bo.getProductSalesName());  //File.ProductSalesName
        mstProductVO.setProductClassification(ProductClsType.GOODS.getCodeDbid());
        mstProductVO.setSalLotSize(BigDecimal.ONE);
        mstProductVO.setPurLotSize(BigDecimal.ONE);
        mstProductVO.setMinSalQty(BigDecimal.ONE);
        mstProductVO.setMinPurQty(BigDecimal.ONE);
        mstProductVO.setProductCd(bo.getProductCode() + bo.getColorCode());//File.ProductCode+File.ColorCode
        mstProductVO.setRegistrationDate(bo.getEffectiveDate());
        mstProductVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        mstProductVO.setStdRetailPrice(bo.getStdRetailPrice());
        mstProductVO.setProductRetrieve(bo.getProductCode() + bo.getColorCode() + bo.getColorName());//File.ProductCode+File.ColorCode+File.ProductSalesName+File.ColorName
        mstProductVO.setAllPath(bo.getProductCode());
        mstProductVO.setAllNm(bo.getProductSalesName());
        mstProductVO.setAllParentId(null);
        mstProductVO.setProductCategoryId(null);
        mstProductVO.setDisplacement(bo.getDisplacement());
        mstProductVO.setProductLevel(CommonConstants.INTEGER_ONE);//level:1

        saveMstProductTaxVOList.add(mstProductVO);
    }

}
