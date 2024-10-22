package com.a1stream.master.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.master.CMM050101BO;
import com.a1stream.domain.bo.master.CMM050101ExportBO;
import com.a1stream.domain.bo.master.CMM050102BO;
import com.a1stream.domain.bo.master.CMM050102BasicInfoBO;
import com.a1stream.domain.bo.master.CMM050102PurchaseControlBO;
import com.a1stream.domain.bo.master.CMM050102SalesControlBO;
import com.a1stream.domain.form.master.CMM050101Form;
import com.a1stream.domain.form.master.CMM050102Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.a1stream.master.service.CMM0501Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@Component
public class CMM0501Facade {

    @Resource
    private CMM0501Service cmm0501Service;

    public Page<CMM050101BO> findPartsInformationInquiryAndPageList(CMM050101Form model, String siteId) {

        return cmm0501Service.findPartsInformationInquiryAndPageList(model, siteId);
    }

    public List<Object> findPartsInformationInquiryList(CMM050101Form model, String siteId) {

        List<CMM050101BO> list = cmm0501Service.findPartsInformationInquiryList(model, siteId);
        List<Object> exportList = new ArrayList<>();
        for (CMM050101BO bo: list) {
            bo.setPartsCd(PartNoUtil.format(bo.getPartsCd()));

            if (bo.getRegistrationDate() != null) {
                LocalDate date = LocalDate.parse(bo.getRegistrationDate(), DateTimeFormatter.BASIC_ISO_DATE);

                String formattedDate = date.format(DateTimeFormatter.ofPattern(CommonConstants.DEFAULT_DATE_FORMAT));

                bo.setRegistrationDate(formattedDate);
            }

            CMM050101ExportBO member = new CMM050101ExportBO();

            member.setBrandCd(bo.getBrandCd());
            member.setPartsCd(bo.getPartsCd());
            member.setPartsNm(bo.getPartsNm());
            member.setLargeGroupNm(bo.getLargeGroupNm());
            member.setMiddleGroupNm(bo.getMiddleGroupNm());
            member.setRegistrationDate(bo.getRegistrationDate());
            member.setPriceExcludeVAT(bo.getPriceExcludeVAT());
            member.setPriceIncludeVAT(bo.getPriceIncludeVAT());
            member.setStdPurchasePrice(bo.getStdPurchasePrice());
            exportList.add(member);
        }

        return exportList;
    }

    public List<CMM050102PurchaseControlBO> getPurchaseControlGridList(CMM050102Form model, String siteId) {

        if(CommonConstants.CHAR_ADD.equals(model.getDataType())
                && CommonConstants.CHAR_FALSE.equals(model.getConfirmFinishFlag())) {
            return cmm0501Service.getPurchaseControlGridAddList(model, siteId);
        }else {
            if(CommonConstants.CHAR_TRUE.equals(model.getConfirmFinishFlag())) {
                siteId = model.getSiteId() !=null ?model.getSiteId():siteId;
                MstProductVO mstProduct = cmm0501Service.findPartsId(siteId, PartNoUtil.formaForDB(model.getPartsCd()));
                model.setPartsId(mstProduct.getProductId());
            }
            return cmm0501Service.getPurchaseControlGridEditList(model, siteId);
        }
    }

    public CMM050102BO getBasicInfoList(CMM050102Form model, String siteId) {

        CMM050102BO cmm050102BO = new CMM050102BO();

        if(CommonConstants.CHAR_EDIT.equals(model.getDataType())) {
            CMM050102BasicInfoBO basicInfo = cmm0501Service.getBasicInfoList(model, siteId);
            CMM050102SalesControlBO salesControl = cmm0501Service.getSaleControlList(model, siteId);
            cmm050102BO.setBasicInfo(basicInfo);
            cmm050102BO.setSalesControl(salesControl);
        }
        List<CMM050102PurchaseControlBO> purchaseControlList = this.getPurchaseControlGridList(model, siteId);
        cmm050102BO.setPurchaseControlList(purchaseControlList);
        return cmm050102BO;
    }

    public void confirmPartsInformationMaintenance(CMM050102Form form, PJUserDetails uc) {

        this.validateData(form, form.getPartsCd(), uc.getDealerCode(),form.getDataType());

        confirmAdd(form, uc);

        if(CommonConstants.CHAR_DEFAULT_SITE_ID.equals(form.getSiteId()) && CommonConstants.CHAR_EDIT.equals(form.getDataType()) ) {
            //当siteId为666N时且为更新操作 更新表reorder_guideline表

            ReorderGuidelineVO reorderGuidelineVO = null;

            List<Long> pointIds = form.getPurchaseControlList().stream()
                    .map(CMM050102PurchaseControlBO::getPointId)
                    .toList();

            List<ReorderGuidelineVO> reorderGuidelineVOList = cmm0501Service.findReorderGuidelineVOList(uc.getDealerCode(), pointIds, form.getPartsId());
            //reorderGuidelineVOList不存在时新增，存在时更新
            List<ReorderGuidelineVO> reorderGuidelineVOAddList = new ArrayList<>();
            for(int i = 0; i <form.getPurchaseControlList().size(); i++) {
                CMM050102PurchaseControlBO bo = form.getPurchaseControlList().get(i);
                Optional<ReorderGuidelineVO> matchingReorderGuideline = reorderGuidelineVOList.stream()
                        .filter(item -> bo.getPointId().equals(item.getFacilityId()))
                        .findFirst();

                ReorderGuidelineVO reorderGuideline = null;

                if(matchingReorderGuideline.isPresent()) {
                    reorderGuideline = matchingReorderGuideline.get();
                }

                if (reorderGuideline == null) {
                    //新增reorderGuideline
                    reorderGuidelineVO = new ReorderGuidelineVO();
                    reorderGuidelineVO.setPartsLeadtime(0);
                    reorderGuidelineVO.setUpdateCount(0);
                    reorderGuidelineVO.setSiteId(uc.getDealerCode());
                } else {
                    //更新reorderGuideline
                    Map<Long, ReorderGuidelineVO> reorderGuidelineVOMap = reorderGuidelineVOList.stream().collect(Collectors.toMap(ReorderGuidelineVO::getReorderGuidelineId, Function.identity()));
                    reorderGuidelineVO = reorderGuidelineVOMap.get(reorderGuideline.getReorderGuidelineId());
                    reorderGuidelineVO.setSiteId(reorderGuideline.getSiteId());
                }
                reorderGuidelineVO.setFacilityId(bo.getPointId());
                reorderGuidelineVO.setProductId(form.getPartsId());
                reorderGuidelineVO.setReorderPoint(bo.getRop() != null ? bo.getRop() : BigDecimal.ZERO);
                reorderGuidelineVO.setReorderQty(bo.getRoq() != null ? bo.getRoq() : BigDecimal.ZERO);
                reorderGuidelineVO.setRopRoqManualFlag(bo.getRopAndRoqSign() != null ? bo.getRopAndRoqSign() : CommonConstants.CHAR_N);
                reorderGuidelineVOAddList.add(reorderGuidelineVO);
            }

            List<ProductInventoryVO> productInventoryVOList = productInventoryVOUpdateList(form, uc);
            Set<Long> afterLocationIds = form.getPurchaseControlList().stream()
                                         .map(CMM050102PurchaseControlBO::getLocationId).collect(Collectors.toSet());
            List<CMM050102PurchaseControlBO> result = this.getPurchaseControlGridList(form, uc.getDealerCode());
            Set<Long> beforeLocationIds = result.stream().map(CMM050102PurchaseControlBO::getLocationId).collect(Collectors.toSet());

            List<ProductInventoryVO> filteredList = productInventoryVOList.stream().filter(vo -> (CommonConstants.CHAR_N.equals(vo.getPrimaryFlag()) && vo.getQuantity().compareTo(BigDecimal.ZERO) == CommonConstants.INTEGER_ZERO) || ObjectUtils.isEmpty(vo.getLocationId())).collect(Collectors.toList());
            productInventoryVOList.removeAll(filteredList);
            Set<Long> productInventoryIds = filteredList.stream().map(ProductInventoryVO::getProductInventoryId).collect(Collectors.toSet());
            List<ProductInventoryVO> removeProductInventory = cmm0501Service.findByProductInventoryIdIn(productInventoryIds);

            cmm0501Service.updateEditUc(reorderGuidelineVO, reorderGuidelineVOAddList, productInventoryVOList,beforeLocationIds,afterLocationIds,removeProductInventory);
        }else if(CommonConstants.CHAR_EDIT.equals(form.getDataType())) {
            //编辑2
            //更新mst_product表
            MstProductVO mstProductVO;
            Long partsId = cmm0501Service.findPartsId(uc.getDealerCode(),form.getPartsCd()) != null
                         ? cmm0501Service.findPartsId(uc.getDealerCode(), form.getPartsCd()).getProductId()
                         : cmm0501Service.findPartsId(CommonConstants.CHAR_DEFAULT_SITE_ID, form.getPartsCd()).getProductId();
            mstProductVO = cmm0501Service.findMstProductVO(partsId);

            MstProductCategoryVO mstProductCategory= cmm0501Service.findByProductCategoryId(form.getMiddleGroup());
            String allParentId = mstProductCategory.getParentCategoryId().toString()
                               + CommonConstants.CHAR_VERTICAL_BAR
                               + mstProductCategory.getProductCategoryId().toString();
            String allPath = mstProductCategory.getAllPath()
                           + CommonConstants.CHAR_VERTICAL_BAR
                           + form.getPartsCd();
            String allNm = mstProductCategory.getAllNm()
                         + CommonConstants.CHAR_VERTICAL_BAR
                         + form.getPartsNm();
            Long brandId = cmm0501Service.findBybrandCd(form.getBrand()).getBrandId();
            List<MstProductVO> mstProductVOList = new ArrayList<>();

            mstProductVO.setSiteId(mstProductVO.getSiteId());
            mstProductVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            mstProductVO.setProductCategoryId(form.getMiddleGroup());
            mstProductVO.setProductCd(form.getPartsCd());
            mstProductVO.setAllParentId(allParentId);
            mstProductVO.setAllPath(allPath);
            mstProductVO.setAllNm(allNm);
            mstProductVO.setRegistrationDate(form.getRegistrationDate());
            mstProductVO.setEnglishDescription(form.getPartsNm());
            mstProductVO.setLocalDescription(form.getLocalNm());
            mstProductVO.setSalesDescription(form.getSalesNm());
            String productProperty = "{\"length\":\"" + form.getLength() + "\",\"width\":\"" + form.getWidth() + "\",\"weight\":\"" + form.getWeight() + "\",\"height\":\"" + form.getHeight() + "\"}";
            mstProductVO.setProductProperty(productProperty);
            mstProductVO.setProductRetrieve((form.getPartsCd()+form.getPartsNm()+form.getSalesNm()).replaceAll("\\s+", "").toUpperCase());
            mstProductVO.setStdRetailPrice(form.getStdRetailPrice() != null ? form.getStdRetailPrice() : BigDecimal.ZERO);
            mstProductVO.setStdWsPrice(new BigDecimal(CommonConstants.CHAR_ZERO));
            mstProductVO.setStdPriceUpdateDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            mstProductVO.setSalLotSize(form.getSalesLot());
            mstProductVO.setPurLotSize(new BigDecimal(CommonConstants.CHAR_ONE));
            mstProductVO.setMinSalQty(form.getMinSalesQty());
            mstProductVO.setMinPurQty(new BigDecimal(CommonConstants.CHAR_ONE));
            mstProductVO.setSalesStatusType(form.getUnavailable());
            mstProductVO.setBrandId(brandId);
            mstProductVO.setBrandCd(form.getBrand());
            mstProductVO.setImageUrl(null);
            mstProductVO.setBatteryFlag(CommonConstants.CHAR_N);
            mstProductVOList.add(mstProductVO);

            List<ProductInventoryVO> productInventoryVOList = new ArrayList<>();
            if(form.getPurchaseControlList() != null) {
                productInventoryVOList = productInventoryVOUpdateList(form, uc);
            }
            //抽出productInventoryVOList中非主货位且数量为0的记录,解决Bug,当A货位->null时，并不会删除这条记录
            List<ProductInventoryVO> filteredList = productInventoryVOList.stream().filter(vo -> (CommonConstants.CHAR_N.equals(vo.getPrimaryFlag()) && vo.getQuantity().compareTo(BigDecimal.ZERO) == CommonConstants.INTEGER_ZERO) || ObjectUtils.isEmpty(vo.getLocationId())).collect(Collectors.toList());
            productInventoryVOList.removeAll(filteredList);
            Set<Long> productInventoryIds = filteredList.stream().map(ProductInventoryVO::getProductInventoryId).collect(Collectors.toSet());
            List<ProductInventoryVO> removeProductInventory = cmm0501Service.findByProductInventoryIdIn(productInventoryIds);

            //afterLocationIds (修改后)
            Set<Long> afterLocationIds = form.getPurchaseControlList().stream()
                                         .map(CMM050102PurchaseControlBO::getLocationId).collect(Collectors.toSet());

            List<CMM050102PurchaseControlBO> result = this.getPurchaseControlGridList(form, uc.getDealerCode());
            Set<Long> beforeLocationIds = result.stream().map(CMM050102PurchaseControlBO::getLocationId).collect(Collectors.toSet());

            cmm0501Service.update(mstProductVO, productInventoryVOList,beforeLocationIds,afterLocationIds,removeProductInventory);
        }
    }

    private List<ProductInventoryVO> productInventoryVOUpdateList(CMM050102Form form, PJUserDetails uc) {
        List<Long> pointIds = form.getPurchaseControlList().stream()
                .map(CMM050102PurchaseControlBO::getPointId)
                .toList();

        List<Long> locationIds = form.getPurchaseControlList().stream()
                .map(CMM050102PurchaseControlBO::getLocationId)
                .toList();

        List<ProductInventoryVO> productInventoryVOList = cmm0501Service.findByFacilityIdsAndProductIdAndSiteId(pointIds, form.getPartsId(), uc.getDealerCode());
        List<ProductInventoryVO> productInventoryList = cmm0501Service
                .findByFacilityIdsAndProductIdAndSiteIdAndLocationIdIn(pointIds, form.getPartsId(), uc.getDealerCode(), locationIds);
        List<ProductInventoryVO> newList = new ArrayList<>(productInventoryVOList);

        List<ProductInventoryVO> productInventoryVOFlagList = cmm0501Service.findByFacilityIdsAndProductIdAndSiteIdAndPrimaryFlag(pointIds, form.getPartsId(), uc.getDealerCode(), CommonConstants.CHAR_Y);

        //main location有值 productInventoryList存在(location不等)时 更新2
        Iterator<ProductInventoryVO> iterator = newList.iterator();
        while (iterator.hasNext()) {
            ProductInventoryVO newItem = iterator.next();
            for (ProductInventoryVO item : productInventoryList) {
                if (newItem.getProductInventoryId().equals(item.getProductInventoryId())) {
                    iterator.remove();
                    break;
                }
            }
        }

        //处理 null -> 主货位A
        if(productInventoryVOList.isEmpty()){
            productInventoryVOList = new ArrayList<>();
        }
        productInventoryOpter(form, uc, productInventoryVOList, productInventoryList, productInventoryVOFlagList);

        //main location有值 productInventoryList存在(location不等)时 更新1
        if (!newList.isEmpty()) {
            for (int i = 0; i < newList.size(); i++) {
                ProductInventoryVO newProductInventory = newList.get(i);
                newProductInventory.setSiteId(uc.getDealerCode());
                newProductInventory.setFacilityId(newProductInventory.getFacilityId());
                newProductInventory.setProductId(form.getPartsId());
                newProductInventory.setPrimaryFlag(CommonConstants.CHAR_N);
                productInventoryVOList.add(newProductInventory);
            }
        }

        //更新2
        if(!productInventoryList.isEmpty()) {
            for (int i = 0; i < productInventoryList.size(); i++) {
                ProductInventoryVO newProductInventory = productInventoryList.get(i);
                newProductInventory.setSiteId(uc.getDealerCode());
                newProductInventory.setFacilityId(newProductInventory.getFacilityId());
                newProductInventory.setProductId(form.getPartsId());
                newProductInventory.setPrimaryFlag(CommonConstants.CHAR_Y);
                productInventoryVOList.add(newProductInventory);
            }
        }

        return productInventoryVOList;
    }

    private void productInventoryOpter(CMM050102Form form, PJUserDetails uc, List<ProductInventoryVO> productInventoryVOList, List<ProductInventoryVO> productInventoryList, List<ProductInventoryVO> productInventoryVOFlagList) {
        if (form.getPurchaseControlList() != null) {
            for (CMM050102PurchaseControlBO gridData : form.getPurchaseControlList()) {
                ProductInventoryVO productInventory = productInventoryList.stream()
                        .filter(pi -> pi.getFacilityId().equals(gridData.getPointId()))
                        .findFirst()
                        .orElse(null);

                ProductInventoryVO productInventoryLocation = productInventoryList.stream()
                        .filter(pi -> pi.getLocationId().equals(gridData.getLocationId()))
                        .findFirst()
                        .orElse(null);

                if (gridData.getMainLocation() != null) {

                    //新增情况 productInventoryVOList存在，productInventoryLocation不存在时才新增，否则在之前已进行更新，将primary_flag由N转化为Y
                    if (productInventoryLocation == null) {
                        ProductInventoryVO productInventoryAddVO = new ProductInventoryVO();
                        productInventoryAddVO.setSiteId(uc.getDealerCode());
                        productInventoryAddVO.setFacilityId(gridData.getPointId());
                        productInventoryAddVO.setProductId(form.getPartsId());
                        productInventoryAddVO.setQuantity(BigDecimal.ZERO);
                        productInventoryAddVO.setLocationId(gridData.getLocationId());
                        productInventoryAddVO.setProductQualityStatusId(null);
                        productInventoryAddVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                        productInventoryAddVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                        productInventoryAddVO.setUpdateCount(0);
                        productInventoryVOList.add(productInventoryAddVO);
                    }
                } else {
                    if (productInventory != null) {
                        productInventory.setPrimaryFlag(CommonConstants.CHAR_N);
                        productInventoryVOList.add(productInventory);
                    }
                }
            }
        }
    }

    private void confirmAdd(CMM050102Form form, PJUserDetails uc) {
        if(CommonConstants.CHAR_ADD.equals(form.getDataType())) {

            MstProductVO mstProductVO = MstProductVO.create();
            MstProductCategoryVO mstProductCategory= cmm0501Service.findByProductCategoryId(form.getMiddleGroup());
            String allParentId = mstProductCategory.getParentCategoryId().toString()
                               + CommonConstants.CHAR_VERTICAL_BAR
                               + mstProductCategory.getProductCategoryId().toString();
            String allPath = mstProductCategory.getAllPath()
                           + CommonConstants.CHAR_VERTICAL_BAR
                           + form.getPartsCd();
            String allNm = mstProductCategory.getAllNm()
                         + CommonConstants.CHAR_VERTICAL_BAR
                         + form.getPartsNm();
            Long brandId = cmm0501Service.findBybrandCd(form.getBrand()).getBrandId();
            List<MstProductVO> mstProductVOList = new ArrayList<>();

            mstProductVO.setSiteId(uc.getDealerCode());
            mstProductVO.setProductClassification(ProductClsType.PART.getCodeDbid());
            mstProductVO.setProductCategoryId(form.getMiddleGroup());
            mstProductVO.setProductCd(form.getPartsCd());
            mstProductVO.setAllParentId(allParentId);
            mstProductVO.setAllPath(allPath);
            mstProductVO.setAllNm(allNm);
            mstProductVO.setRegistrationDate(form.getRegistrationDate());
            mstProductVO.setEnglishDescription(form.getPartsNm());
            mstProductVO.setLocalDescription(form.getLocalNm());
            mstProductVO.setSalesDescription(form.getSalesNm());
            String productProperty = "{\"length\":\"" + form.getLength() + "\",\"width\":\"" + form.getWidth() + "\",\"weight\":\"" + form.getWeight() + "\",\"height\":\"" + form.getHeight() + "\"}";
            mstProductVO.setProductProperty(productProperty);
            mstProductVO.setProductRetrieve((form.getPartsCd()+form.getPartsNm()+form.getSalesNm()).replaceAll("\\s+", "").toUpperCase());
            mstProductVO.setStdRetailPrice(form.getStdRetailPrice() != null ? form.getStdRetailPrice() : BigDecimal.ZERO);
            mstProductVO.setStdWsPrice(new BigDecimal(CommonConstants.CHAR_ZERO));
            mstProductVO.setStdPriceUpdateDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            mstProductVO.setSalLotSize(form.getSalesLot());
            mstProductVO.setPurLotSize(new BigDecimal(CommonConstants.CHAR_ONE));
            mstProductVO.setMinSalQty(form.getMinSalesQty());
            mstProductVO.setMinPurQty(new BigDecimal(CommonConstants.CHAR_ONE));
            mstProductVO.setSalesStatusType(form.getUnavailable());
            mstProductVO.setBrandId(brandId);
            mstProductVO.setBrandCd(form.getBrand());
            mstProductVO.setImageUrl(null);
            mstProductVO.setBatteryFlag(CommonConstants.CHAR_N);
            mstProductVOList.add(mstProductVO);

            List<ProductInventoryVO> productInventoryVOList = new ArrayList<>();

            //设置location表的主货位字段
            Set<Long> locationIds = form.getGridDataList().stream()
                                        .map(CMM050102PurchaseControlBO::getLocationId)
                                        .collect(Collectors.toSet());

            for (int i = 0; i < form.getGridDataList().size(); i++) {
                CMM050102PurchaseControlBO gridData = form.getGridDataList().get(i);
                if (gridData.getMainLocation() != null) {
                    ProductInventoryVO productInventoryVO = new ProductInventoryVO();
                    productInventoryVO.setSiteId(uc.getDealerCode());
                    productInventoryVO.setFacilityId(gridData.getPointId());
                    productInventoryVO.setProductId(mstProductVO.getProductId());
                    productInventoryVO.setQuantity(new BigDecimal(CommonConstants.CHAR_ZERO));
                    productInventoryVO.setLocationId(gridData.getLocationId());
                    productInventoryVO.setProductQualityStatusId(null);
                    productInventoryVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                    productInventoryVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                    productInventoryVOList.add(productInventoryVO);
                }
            }

            //更新Location的主货位

            cmm0501Service.update(mstProductVO, productInventoryVOList,new HashSet<>(),locationIds,null);
        }
    }

    private void validateData(CMM050102Form form, String partsCd, String siteId,String dataType) {

        if(CommonConstants.CHAR_ADD.equals(dataType)) {
            List<String> siteIds = new ArrayList<>();
            siteIds.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
            siteIds.add(siteId);
            MstProductVO mstProduct = cmm0501Service.findFirstByProductCdAndSiteIdIn(partsCd, siteIds);
            //M.E.00200
            if (NumberUtil.le(form.getSalesLot(), BigDecimal.ZERO)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
                                                 CodedMessageUtils.getMessage("label.salesLot"),
                                                 CommonConstants.CHAR_ZERO}));
            }

            if (NumberUtil.le(form.getMinSalesQty(), BigDecimal.ZERO)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200", new String[]{
                                                 CodedMessageUtils.getMessage("label.minSalesQuantity"),
                                                 CommonConstants.CHAR_ZERO}));
            }

            //M.E.00309
            if (mstProduct != null && mstProduct.getProductCd() != null && !mstProduct.getProductCd().isEmpty()) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[]{
                                                 CodedMessageUtils.getMessage("label.partsNo"),
                                                 partsCd,
                                                 CodedMessageUtils.getMessage("label.tableProduct")}));
            }
        }

        //M.E.00303
        if (form.getMainLocation()!=null) {

            LocationVO location = cmm0501Service.findByPointIdAndSiteIdAndMainLocation(form.getPointId(),siteId,form.getMainLocation());

            if (Nulls.isNull(location)) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{
                    CodedMessageUtils.getMessage("label.mainLocation"),
                    form.getMainLocation(),
                    CodedMessageUtils.getMessage("label.tableLocationInfo")}));
            }
        }
    }
}

