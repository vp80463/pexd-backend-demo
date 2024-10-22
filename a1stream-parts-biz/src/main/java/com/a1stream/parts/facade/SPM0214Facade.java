package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.ReturnRequestStatus;
import com.a1stream.common.constants.PJConstants.ReturnRequestType;
import com.a1stream.common.constants.PJConstants.RopRoqParameter;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.common.logic.ConstantsLogic;
import com.a1stream.common.model.ConstantsBO;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.SPM021401BO;
import com.a1stream.domain.bo.parts.SPM021402BO;
import com.a1stream.domain.form.parts.SPM021401Form;
import com.a1stream.domain.form.parts.SPM021402Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.PartsRopqMonthlyVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.a1stream.domain.vo.ReturnRequestItemVO;
import com.a1stream.domain.vo.ReturnRequestListVO;
import com.a1stream.parts.service.SPM0214Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/24   Ruan Hansheng     New
*/
@Component
public class SPM0214Facade {

    @Resource
    private ConstantsLogic constantsLogic;

    @Resource
    private SPM0214Service spm0214Service;

    @Resource
    private HelperFacade helperFacade;

    public List<SPM021401BO> getReturnRequestListList(SPM021401Form form, PJUserDetails uc) {

        List<SPM021401BO> resultList = spm0214Service.getReturnRequestListList(form, uc);
        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ReturnRequestStatus.CODE_ID);
        for (SPM021401BO bo : resultList) {
            bo.setRequestStatus(codeMap.get(bo.getRequestStatus()));
        }
        return resultList;
    }

    public SPM021401BO getReturnRequestItemList(SPM021402Form form, PJUserDetails uc) {

        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ReturnRequestStatus.CODE_ID);

        List<ConstantsBO> returnRequestType = constantsLogic.getConstantsData(PJConstants.ReturnRequestType.class.getDeclaredFields());

        for(ConstantsBO constantsBO : returnRequestType){

            codeMap.put(constantsBO.getCodeDbid(), constantsBO.getCodeData1());
        }

        SPM021401BO returnRequestList = spm0214Service.getReturnRequestList(form, uc);
        Long pointId = returnRequestList.getPointId();
        String siteId = uc.getDealerCode();
        MstFacilityVO mstFacilityVO = spm0214Service.getMstFacilityVO(pointId);
        returnRequestList.setPoint(mstFacilityVO.getFacilityCd() + CommonConstants.CHAR_SPACE + mstFacilityVO.getFacilityNm());
        returnRequestList.setRequestStatus(codeMap.get(returnRequestList.getRequestStatus()));

        form.setPointId(pointId);
        List<SPM021402BO> tableDataList = spm0214Service.getReturnRequestItemList(form, uc);
        List<Long> partsIdList = tableDataList.stream().map(SPM021402BO::getPartsId).collect(Collectors.toList());

        // productCost
        List<String> siteIdList = Arrays.asList(siteId, CommonConstants.CHAR_DEFAULT_SITE_ID);
        List<ProductCostVO> productCostVOList = spm0214Service.getProductCostVOList(partsIdList, CostType.AVERAGE_COST, siteIdList);
        Map<Long, ProductCostVO> productCostVOMap = productCostVOList.stream().collect(Collectors.toMap(ProductCostVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        // productInventory
        List<ProductInventoryVO> productInventoryVOList = spm0214Service.getProductInventoryVOList(siteId, pointId, partsIdList);
        Map<Long, ProductInventoryVO> productInventoryVOMap = productInventoryVOList.stream().collect(Collectors.toMap(ProductInventoryVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        // location
        Set<Long> locationIdSet = productInventoryVOList.stream().map(ProductInventoryVO::getLocationId).collect(Collectors.toSet());
        List<LocationVO> lcoationVOList = spm0214Service.getLocationVOList(locationIdSet);
        Map<Long, LocationVO> locationVOMap = lcoationVOList.stream().collect(Collectors.toMap(LocationVO::getLocationId, Function.identity(), (existingValue, newValue) -> newValue));

        // productStockStatus
        List<ProductStockStatusVO> productStockStatusVOList = spm0214Service.getProductStockStatusVOList(siteId, pointId, partsIdList);
        Map<Long, ProductStockStatusVO> productStockStatusVOMap = productStockStatusVOList.stream().collect(Collectors.toMap(ProductStockStatusVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        // reorderGuideline
        List<ReorderGuidelineVO> reorderGuidelineVOList = spm0214Service.getGuidelineVOList(siteId, pointId, partsIdList);
        Map<Long, ReorderGuidelineVO> reorderGuidelineVOMap = reorderGuidelineVOList.stream().collect(Collectors.toMap(ReorderGuidelineVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        // partsRopqMonthlyJ1
        List<PartsRopqMonthlyVO> partsRopqMonthlyJ1VOList = spm0214Service.getPartsRopqMonthlyVOList(siteId, partsIdList, RopRoqParameter.KEY_PARTSJ1TOTAL);
        Map<Long, PartsRopqMonthlyVO> partsRopqMonthlyJ1VOMap = partsRopqMonthlyJ1VOList.stream().collect(Collectors.toMap(PartsRopqMonthlyVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        // partsRopqMonthlyJ2
        List<PartsRopqMonthlyVO> partsRopqMonthlyJ2VOList = spm0214Service.getPartsRopqMonthlyVOList(siteId, partsIdList, RopRoqParameter.KEY_PARTSJ2TOTAL);
        Map<Long, PartsRopqMonthlyVO> partsRopqMonthlyJ2VOMap = partsRopqMonthlyJ2VOList.stream().collect(Collectors.toMap(PartsRopqMonthlyVO::getProductId, Function.identity(), (existingValue, newValue) -> newValue));

        for (SPM021402BO bo : tableDataList) {
            bo.setParts(PartNoUtil.format(bo.getPartsNo()) + CommonConstants.CHAR_SPACE + (bo.getPartsNm() != null ? bo.getPartsNm() : CommonConstants.CHAR_BLANK));
            bo.setPartsNo(bo.getPartsNo());
            bo.setPartsNm(bo.getPartsNm());
            bo.setRequestType(codeMap.get(bo.getRequestType()));
            bo.setReturnAmount(bo.getRequestQty().multiply(bo.getReturnPrice()));
            
            Long productId = bo.getPartsId();
            ProductCostVO productCostVO = productCostVOMap.get(productId);
            bo.setCost(productCostVO != null ? productCostVO.getCost() : BigDecimal.ZERO);
            bo.setTotalCost(productCostVO != null ? productCostVO.getCost().multiply(bo.getRequestQty()) : BigDecimal.ZERO);
            
            ProductInventoryVO productInventoryVO = productInventoryVOMap.get(productId);
            bo.setMainLocation((productInventoryVO != null && locationVOMap.get(productInventoryVO.getLocationId()) != null) ? locationVOMap.get(productInventoryVO.getLocationId()).getLocationCd() : CommonConstants.CHAR_BLANK);
            
            ProductStockStatusVO productStockStatusVO = productStockStatusVOMap.get(productId);
            bo.setOnHandQty(productStockStatusVO != null ? productStockStatusVO.getQuantity() : BigDecimal.ZERO);
            
            ReorderGuidelineVO reorderGuidelineVO = reorderGuidelineVOMap.get(productId);
            bo.setRop(reorderGuidelineVO != null ? reorderGuidelineVO.getReorderPoint() : BigDecimal.ZERO);
            bo.setRoq(reorderGuidelineVO != null ? reorderGuidelineVO.getReorderQty() : BigDecimal.ZERO);
            
            PartsRopqMonthlyVO partsRopqMonthlyJ1VO = partsRopqMonthlyJ1VOMap.get(productId);
            bo.setJ1(partsRopqMonthlyJ1VO != null ? partsRopqMonthlyJ1VO.getStringValue() : CommonConstants.CHAR_BLANK);
            
            PartsRopqMonthlyVO partsRopqMonthlyJ2VO = partsRopqMonthlyJ2VOMap.get(productId);
            bo.setJ2(partsRopqMonthlyJ2VO != null ? partsRopqMonthlyJ2VO.getStringValue() : CommonConstants.CHAR_BLANK);
        }        
        
        returnRequestList.setTableDataList(tableDataList);
        return returnRequestList;
    }

    public void confirm(SPM021402Form form) {
        
        List<SPM021402BO> updateList = form.getTableDataList().getUpdateRecords();
        List<Long> returnRequestItemIdList = updateList.stream().map(SPM021402BO::getReturnRequestItemId).collect(Collectors.toList());
        List<ReturnRequestItemVO> returnRequestItemVOList = spm0214Service.getReturnRequestItemVOList(returnRequestItemIdList);
        Map<Long, ReturnRequestItemVO> returnRequestItemVOMap = returnRequestItemVOList.stream().collect(Collectors.toMap(ReturnRequestItemVO::getReturnRequestItemId, Function.identity(), (existingValue, newValue) -> newValue));
        for (SPM021402BO bo : updateList) {
            ReturnRequestItemVO vo = returnRequestItemVOMap.get(bo.getReturnRequestItemId());
            if (!bo.getUpdateCount().equals(vo.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.partsNo"), bo.getPartsNo(), ComUtil.t("title.partsReturnRequest_02")}));
            }
            vo.setRequestQty(bo.getRequestQty());
        }
        
        spm0214Service.confirm(returnRequestItemVOList);
    }

    public void issue(SPM021402Form form) {
        
        List<SPM021402BO> allTableDataList = form.getAllTableDataList();
        List<Long> returnRequestItemIdList = allTableDataList.stream().map(SPM021402BO::getReturnRequestItemId).collect(Collectors.toList());
        List<ReturnRequestItemVO> returnRequestItemVOList = spm0214Service.getReturnRequestItemVOList(returnRequestItemIdList);
        Map<Long, ReturnRequestItemVO> returnRequestItemVOMap = returnRequestItemVOList.stream().collect(Collectors.toMap(ReturnRequestItemVO::getReturnRequestItemId, Function.identity(), (existingValue, newValue) -> newValue));
        for (SPM021402BO bo : allTableDataList) {
            ReturnRequestItemVO vo = returnRequestItemVOMap.get(bo.getReturnRequestItemId());
            if (!bo.getUpdateCount().equals(vo.getUpdateCount())) {
                throw new BusinessCodedException(ComUtil.t("M.E.00314", new String[] {ComUtil.t("label.partsNo"), bo.getPartsNo(), ComUtil.t("title.partsReturnRequest_02")}));
            }
            vo.setRequestQty(bo.getRequestQty());
            vo.setRequestStatus(bo.getApproveQty().compareTo(BigDecimal.ZERO) > 0 ? ReturnRequestStatus.REQUESTED.getCodeDbid() : ReturnRequestStatus.COMPLETED.getCodeDbid());
        }

        ReturnRequestListVO returnRequestListVO = spm0214Service.getReturnRequestListVO(form.getReturnRequestListId());
        BigDecimal totalRequestQty = returnRequestItemVOList.stream().map(ReturnRequestItemVO::getRequestQty).reduce(BigDecimal.ZERO, BigDecimal::add);
        returnRequestListVO.setRequestStatus(totalRequestQty.compareTo(BigDecimal.ZERO) > 0 ? ReturnRequestStatus.REQUESTED.getCodeDbid() : ReturnRequestStatus.COMPLETED.getCodeDbid());
        returnRequestListVO.setRequestDate(DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_YMD));
        
        spm0214Service.issue(returnRequestItemVOList, returnRequestListVO);
    }

    public SPM021402Form picking(SPM021402Form form, PJUserDetails uc) {
        
        return spm0214Service.picking(form, uc);
    }
}
