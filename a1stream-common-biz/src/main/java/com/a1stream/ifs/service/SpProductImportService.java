package com.a1stream.ifs.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.ProductRelationClass;
import com.a1stream.common.exception.PJCustomException;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.entity.MstBrand;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.MstProductCategory;
import com.a1stream.domain.entity.MstProductRelation;
import com.a1stream.domain.repository.MstBrandRepository;
import com.a1stream.domain.repository.MstProductCategoryRepository;
import com.a1stream.domain.repository.MstProductRelationRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductRelationVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.ifs.bo.SpProductBO;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CollectionUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.StringUtils;

import jakarta.annotation.Resource;

@Service
public class SpProductImportService {

    @Resource
    private MstProductRelationRepository mstProductRelationRepository;
    @Resource
    private MstProductCategoryRepository productCategoryRepository;
    @Resource
    private MstProductRepository productRepository;
    @Resource
    private MstBrandRepository brandRepository;

    public List<MstProductCategoryVO> getProductCategoryVOListBSerialCd(String productClassification
                                                                        , String activeFlg
                                                                        , String categoryTypeCd
                                                                        , Set<String> categoryCdSet) {

        List<MstProductCategory> productCategoryList = productCategoryRepository.findByCategoryTypeAndProductClassificationAndActiveFlagAndCategoryCdIn(categoryTypeCd
                                                                                                                                                      , productClassification
                                                                                                                                                      , activeFlg
                                                                                                                                                      , categoryCdSet);
        return BeanMapUtils.mapListTo(productCategoryList, MstProductCategoryVO.class);
    }

    public List<MstProductVO> getPartsMstProductVOByCodeIn(List<String> partsNoList) {

        List<MstProductVO> mstProductVOList = new ArrayList<>();
        List<MstProduct> productList = new ArrayList<>();

        productList = productRepository.findBySiteIdAndProductClassificationAndProductCdIn(CommonConstants.CHAR_DEFAULT_SITE_ID
                                                                                            , ProductClsType.PART.getCodeDbid()
                                                                                            , partsNoList);
        if(CollectionUtils.isNotEmpty(productList)) {

            mstProductVOList = BeanMapUtils.mapListTo(productList, MstProductVO.class);
        }

        return mstProductVOList;
    }

    public List<MstProductRelationVO> getPartsMstProductRelationVOByToProdRelationClass(Set<Long> toProductIds, String relationClass) {

        List<MstProductRelationVO> mstProductRelationVOList = new ArrayList<>();
        List<MstProductRelation> productRelationList = new ArrayList<>();

        productRelationList = mstProductRelationRepository.findByRelationTypeAndToProductIdIn(relationClass, toProductIds);
        mstProductRelationVOList = BeanMapUtils.mapListTo(productRelationList, MstProductRelationVO.class);

        return mstProductRelationVOList;
    }

    //根据查询出来的parts信息进行更新或者插入
    public void savePartsAndToLocal(Set<SpProductBO> insertSet
                                 , Set<SpProductBO> updateSet
                                 , Map<String, MstProductVO> productVOMap
                                 , Map<String, MstProductCategoryVO> productCategoryVOMap
                                 , Map<Long, MstProductRelationVO> productRelationAllVOMap
                                 , Map<String, String> partsCategoryMap){

        List<MstProductVO> insertProductVOList = new ArrayList<>();
        List<MstProductRelationVO> insertProductRelationVOList = new ArrayList<>();

        //for cmmToLocal BO
        Map<String, String> insertProductRelationMap = new HashMap<>();
        Map<String, String> updateProductRelationMap = new HashMap<>();
        Set<String> supersedingPartsNoSet = new HashSet<>();

        MstBrand mstBrand = brandRepository.findBySiteIdAndDefaultFlag(CommonConstants.CHAR_DEFAULT_SITE_ID, CommonConstants.CHAR_SUPPER_ADMIN_FLAG);

        //处理新增的productList
        this.insertPartsInfo(insertSet
                           , insertProductVOList
                           , insertProductRelationVOList
                           , productVOMap
                           , insertProductRelationMap
                           , supersedingPartsNoSet
                           , productCategoryVOMap
                           , mstBrand
                           );

        //处理更新的productVO && productRelationVO
        this.updatePartsInfo(updateSet
                            , insertProductRelationVOList
                            , productVOMap
                            , productRelationAllVOMap
                            , insertProductRelationMap
                            , updateProductRelationMap
                            , supersedingPartsNoSet
                            , productCategoryVOMap
                            , mstBrand);

        if(!insertProductRelationVOList.isEmpty()) {
            //save insertProductRelation
            this.saveMstProductRelationVOList(insertProductRelationVOList);
        }
    }

    private void insertPartsInfo(Set<SpProductBO> insertSet
                                    , List<MstProductVO> insertProductVOList
                                    , List<MstProductRelationVO> insertProductRelationVOList
                                    , Map<String, MstProductVO> productVOMap
                                    , Map<String, String> insertProductRelationMap
                                    , Set<String> supersedingPartsNoSet
                                    , Map<String,MstProductCategoryVO> productCategoryVOMap
                                    , MstBrand mstBrand) {

        String supersedingPartNo;
        MstProductVO productVO;
        MstProductRelationVO productRelationAllVO;
        Long fromProductId;
        Long toProductId;
        String insertPartNo;

        if(!insertSet.isEmpty()) {

            for(SpProductBO insertPartsInfo : insertSet) {

                productVO = null;

                //新增mstproduct
                productVO = this.createOrUpdatePartsProductVO(insertPartsInfo, productVO, productCategoryVOMap, mstBrand);

                insertProductVOList.add(productVO);
            }
            //save insertProduct
            //insertProductVOList = this.saveMstProductVOList(insertProductVOList);

            productVOMap.putAll(insertProductVOList.stream().collect(Collectors.toMap(MstProductVO::getProductCd, t->t)));

            //新增insertSet相应替代关系表
            for(SpProductBO insertPartsInfo : insertSet) {

                insertPartNo = insertPartsInfo.getPartNo();
                supersedingPartNo = insertPartsInfo.getSupersedingPart().trim();

                if(!supersedingPartNo.isEmpty()) {

                    if(productVOMap.get(supersedingPartNo) == null) {

                        String message = "ME.00204";
                        throw new PJCustomException(message);
                    } else {

                        fromProductId = productVOMap.get(supersedingPartNo).getProductId();
                        toProductId = productVOMap.get(insertPartNo).getProductId();

                        productRelationAllVO = this.createNewProductRelationVO(fromProductId, toProductId, ProductRelationClass.SUPERSEDING);

                        insertProductRelationVOList.add(productRelationAllVO);
                        //for cmmtoLocal BO
                        supersedingPartsNoSet.add(insertPartNo);
                        supersedingPartsNoSet.add(supersedingPartNo);

                        insertProductRelationMap.put(insertPartNo, supersedingPartNo);
                    }
                }
            }

            if(!insertProductVOList.isEmpty()) {
                this.saveMstProductVOList(insertProductVOList);
            }
        }
    }

    private void updatePartsInfo(Set<SpProductBO> updateSet
                                , List<MstProductRelationVO> insertProductRelationVOList
                                , Map<String, MstProductVO> productVOMap
                                , Map<Long, MstProductRelationVO> productRelationAllVOMap
                                , Map<String, String> insertProductRelationMap
                                , Map<String, String> updateProductRelationMap
                                , Set<String> supersedingPartsNoSet
                                , Map<String,MstProductCategoryVO> productCategoryVOMap
                                , MstBrand mstBrand) {

        String supersedingPartNo;
        MstProductVO productVO;
        MstProductRelationVO productRelationAllVO;
        Long fromProductId;
        Long toProductId;

        if(!updateSet.isEmpty()) {

            List<MstProductRelationVO> deleteProductRelationVOList = new ArrayList<>();
            List<MstProductVO> updateProductVOList = new ArrayList<>();
            List<MstProductRelationVO> updateProductRelationVOList = new ArrayList<>();
            String updatePartNo;

            for(SpProductBO updatePartsInfo : updateSet) {

                updatePartNo = updatePartsInfo.getPartNo();
                productVO = productVOMap.get(updatePartNo);

                //更新 product
                this.createOrUpdatePartsProductVO(updatePartsInfo, productVO, productCategoryVOMap, mstBrand);

                updateProductVOList.add(productVO);

                //update ProductRelation
                if(StringUtils.isEmpty(updatePartsInfo.getSupersedingPart()) && productRelationAllVOMap.containsKey(productVOMap.get(updatePartNo).getProductId())) {
                    //取消原有替代关系
                    deleteProductRelationVOList.add(productRelationAllVOMap.get(productVOMap.get(updatePartNo).getProductId()));

                    //for Local BO
                    updateProductRelationMap.put(updatePartNo, CommonConstants.CHAR_BLANK);
                    supersedingPartsNoSet.add(updatePartNo);
                } else if(StringUtils.isNotEmpty(updatePartsInfo.getSupersedingPart())) {

                    supersedingPartNo = updatePartsInfo.getSupersedingPart();
                    //get新件的prodId
                    fromProductId = productVOMap.get(supersedingPartNo).getProductId();
                    //get旧件的prodId
                    toProductId = productVOMap.get(updatePartNo).getProductId();

                    if(productVOMap.get(supersedingPartNo) == null) {

                        String message = "ME.00204";
                        throw new PJCustomException(message);
                    }

                    if(productRelationAllVOMap.containsKey(toProductId)) {
                        //更新替代关系
                        productRelationAllVO = productRelationAllVOMap.get(toProductId);

                        //fromProductId = productVOMap.get(supersedingPartNo).getProductId();

                        productRelationAllVO.setFromProductId(fromProductId);
                        productRelationAllVO.setFromDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));

                        updateProductRelationVOList.add(productRelationAllVO);
                        //for Local BO
                        updateProductRelationMap.put(updatePartNo, supersedingPartNo);
                        supersedingPartsNoSet.add(updatePartNo);
                        supersedingPartsNoSet.add(supersedingPartNo);
                    } else {

                        productRelationAllVO = this.createNewProductRelationVO(fromProductId, toProductId, ProductRelationClass.SUPERSEDING);
                        insertProductRelationVOList.add(productRelationAllVO);

                        //for Local BO
                        insertProductRelationMap.put(updatePartNo, supersedingPartNo);
                        supersedingPartsNoSet.add(updatePartNo);
                        supersedingPartsNoSet.add(supersedingPartNo);
                    }
                }
            }
            //保存
            if(CollectionUtils.isNotEmpty(deleteProductRelationVOList)) {
                this.deleteMstProductRelationVOList(deleteProductRelationVOList);
            }

            if(CollectionUtils.isNotEmpty(updateProductVOList)) {
                this.saveMstProductVOList(updateProductVOList);
            }

            if(CollectionUtils.isNotEmpty(updateProductRelationVOList)) {
                this.saveMstProductRelationVOList(updateProductRelationVOList);
            }
        }
    }

    public void deleteMstProductRelationVOList(List<MstProductRelationVO> productRelationVOList) {

        List<MstProductRelation> deleteProductRelationList = BeanMapUtils.mapListTo(productRelationVOList, MstProductRelation.class);
        mstProductRelationRepository.deleteAllInBatch(deleteProductRelationList);
    }

    private MstProductRelationVO createNewProductRelationVO(Long fromProductId, Long toProductId, String relationClass) {

        MstProductRelationVO productRelationVO = MstProductRelationVO.create();
        productRelationVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
        productRelationVO.setFromDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMD_NODELIMITER));
        productRelationVO.setToDate(CommonConstants.MAX_DATE);
        productRelationVO.setFromProductId(fromProductId);
        productRelationVO.setToProductId(toProductId);
        productRelationVO.setRelationType(relationClass);
        productRelationVO.setUpdateCount(CommonConstants.INTEGER_ZERO);

        return productRelationVO;
    }

    private MstProductVO createOrUpdatePartsProductVO(SpProductBO insertPartsInfo
                                                    , MstProductVO productVO
                                                    , Map<String,MstProductCategoryVO> productCategoryVOMap
                                                    , MstBrand mstBrand) {

        String maxPackLot = maxPackLot(insertPartsInfo.getLots().size() >=1 ? insertPartsInfo.getLots().get(0) : CommonConstants.CHAR_ZERO
                                      ,insertPartsInfo.getLots().size() >=2 ? insertPartsInfo.getLots().get(1) : CommonConstants.CHAR_ZERO
                                      ,insertPartsInfo.getLots().size() >=3 ? insertPartsInfo.getLots().get(2) : CommonConstants.CHAR_ZERO);
//        JSONObject productPropertyJson;

//        String stringValue = getStringValue(spPartsInfoItemModel.getPackLot1(),spPartsInfoItemModel.getPackLot2(),spPartsInfoItemModel.getPackLot3());
//        this.createProductFeature(cmmProduct, defaultFcMap, stringValue,ProductFeatureCategorySub.KEY_PARTPACKINGLOT);

        if(productVO == null) {
            productVO = MstProductVO.create();

            productVO.setSiteId(CommonConstants.CHAR_DEFAULT_SITE_ID);
            productVO.setProductCd(insertPartsInfo.getPartNo());
            productVO.setUpdateCount(CommonConstants.INTEGER_ZERO);
        }

        String productProperty = "{\"length\":\"" + insertPartsInfo.getPartSizeL()
                                 + "\",\"width\":\"" + insertPartsInfo.getPartSizeW()
                                 + "\",\"weight\":\"" + insertPartsInfo.getPartWeight()
                                 + "\",\"height\":\"" + insertPartsInfo.getPartSizeH() + "\"}";

//        productPropertyJson = new JSONObject();
//
//        productPropertyJson.put(ProductProperty.PARTLENGTH, insertPartsInfo.getPartSizeL());
//        productPropertyJson.put(ProductProperty.PARTWIDTH, insertPartsInfo.getPartSizeW());
//        productPropertyJson.put(ProductProperty.PARTHEIGHT, insertPartsInfo.getPartSizeH());
//        productPropertyJson.put(ProductProperty.PARTWEIGHT, insertPartsInfo.getPartWeight());
        productVO.setProductProperty(productProperty);

        String allPath = productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getParentCategoryCd()
                                                        .concat(CommonConstants.CHAR_VERTICAL_BAR)
                                                        .concat(insertPartsInfo.getProductCategory())
                                                        .concat(CommonConstants.CHAR_VERTICAL_BAR)
                                                        .concat(insertPartsInfo.getPartNo());

        String allName = productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getParentCategoryNm()
                                                        .concat(CommonConstants.CHAR_VERTICAL_BAR)
                                                        .concat(productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getCategoryNm())
                                                        .concat(CommonConstants.CHAR_VERTICAL_BAR)
                                                        .concat(insertPartsInfo.getPartNameNative());

        productVO.setEnglishDescription(insertPartsInfo.getPartNameEng());
        productVO.setLocalDescription(insertPartsInfo.getPartNameNative());
        productVO.setSalesDescription(insertPartsInfo.getPartNameNative());
        productVO.setProductClassification(ProductClsType.PART.getCodeDbid());
        productVO.setStdRetailPrice(insertPartsInfo.getRetailStandardPrice());
        productVO.setStdWsPrice(insertPartsInfo.getWholesaleStandardPrice());
        productVO.setRegistrationDate(insertPartsInfo.getRegisterDate());
        productVO.setBrandId(mstBrand.getBrandId());
        productVO.setBrandCd(mstBrand.getBrandCd());
        productVO.setSalesStatusType(insertPartsInfo.getNonsalesId());
        if (StringUtils.equals(insertPartsInfo.getNonsalesId(), CommonConstants.CHAR_TWO)) {
            productVO.setSalesStatusType(CommonConstants.CHAR_ONE);
        }
        productVO.setProductCategoryId(productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getProductCategoryId());
        productVO.setAllPath(allPath);
        productVO.setAllNm(allName);

        productVO.setProductRetrieve(insertPartsInfo.getPartNo().toUpperCase()
                                    +insertPartsInfo.getPartNameEng().toUpperCase()
                                    +insertPartsInfo.getPartNameNative().toUpperCase());

        productVO.setAllParentId(productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getParentCategoryId().toString()
                                    .concat(CommonConstants.CHAR_VERTICAL_BAR)
                                    .concat(productCategoryVOMap.get(insertPartsInfo.getProductCategory()).getProductCategoryId().toString()));

        if (StringUtils.isNotBlankText(insertPartsInfo.getOptionCode1()) && StringUtils.equals(CommonConstants.CHAR_OPTION_CODE,insertPartsInfo.getOptionCode1())) {
            productVO.setBatteryFlag(CommonConstants.CHAR_YES);

        }

        productVO.setPurLotSize(StringUtils.isBlankText(insertPartsInfo.getMinSalesLot())
                ? NumberUtil.toBigDecimal(maxPackLot) : NumberUtil.toBigDecimal(insertPartsInfo.getMinSalesLot()));
        productVO.setMinPurQty(StringUtils.isBlankText(insertPartsInfo.getMinSalesLot())
                ? NumberUtil.toBigDecimal(maxPackLot) : NumberUtil.toBigDecimal(insertPartsInfo.getMinSalesLot()));
        productVO.setSalLotSize(CommonConstants.BIGDECIMAL_ONE);
        productVO.setMinSalQty(CommonConstants.BIGDECIMAL_ONE);
        productVO.setStdPriceUpdateDate(insertPartsInfo.getRetailPriceRevisionDate());

        return productVO;
    }

    private String maxPackLot(String packLot1,String packLot2,String packLot3) {

        String maxPackLot = CommonConstants.CHAR_ZERO;
        if (StringUtils.compare(StringUtils.isBlankText(packLot1) ? CommonConstants.CHAR_ZERO : packLot1
                        , StringUtils.isBlankText(packLot2) ? CommonConstants.CHAR_ZERO : packLot2,true) > 0) {

            maxPackLot = packLot1;
        } else {

            maxPackLot = packLot2;
        }

        if (StringUtils.compare(StringUtils.isBlankText(maxPackLot) ? CommonConstants.CHAR_ZERO : maxPackLot
                , StringUtils.isBlankText(packLot3) ? CommonConstants.CHAR_ZERO : packLot3,true) < 0) {

            maxPackLot = packLot3;
        }

        return StringUtils.isBlankText(maxPackLot) ? CommonConstants.CHAR_ZERO : maxPackLot;
    }

    public List<MstProductVO> saveMstProductVOList(List<MstProductVO> productVOList) {

        List<MstProduct> productList = BeanMapUtils.mapListTo(productVOList, MstProduct.class);
        productRepository.saveInBatch(productList);

        return BeanMapUtils.mapListTo(productList, MstProductVO.class);
    }

    public void saveMstProductRelationVOList(List<MstProductRelationVO> productRelationVOList) {

        List<MstProductRelation> productRelationList = BeanMapUtils.mapListTo(productRelationVOList, MstProductRelation.class);
        mstProductRelationRepository.saveInBatch(productRelationList);

    }
}
