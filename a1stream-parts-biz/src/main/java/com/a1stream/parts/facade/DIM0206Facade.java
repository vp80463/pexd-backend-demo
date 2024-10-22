package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.CostType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.DIM020601BO;
import com.a1stream.domain.form.parts.DIM020601Form;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductCostVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.ProductStockStatusBackupVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.parts.service.DIM0206Service;
import com.alibaba.excel.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Stock Import
*
* @author mid2178
*/
@Component
public class DIM0206Facade {

    @Resource
    private DIM0206Service dim0206Service;

    public DIM020601Form checkFile(DIM020601Form form, String siteId) {

        // 根据条件部的point,如果未处于盘点中，提示信息
        checkStockTaking(form.getPointId(), siteId);

        // 根据条件部的point,校验完成情况
        checkOrder(form.getPointId(), siteId);

        // 将导入的数据和error信息设置到画面上
        return setSreenList(form, siteId);
    }

    private DIM020601Form setSreenList(DIM020601Form form, String siteId) {

        List<DIM020601BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        // 用于前台校验:如果在product_inventory存在数据，confirm前在画面弹提示
        form.setCheckFlag(checkStockLine(siteId, form.getPointId()));

        setPointCode(form);

        // 转大写 去除前后空格
        fromatForDB(importList);

        Set<String> seen = new HashSet<>();
        Map<String, MstFacilityVO> facilityMap = getFacilityMap(siteId, importList);
        Map<String, LocationVO> locationMap = getLocationMap(siteId, form.getPointId(), importList);
        Map<String, MstProductVO> productMap = getProductMap(siteId, importList);

        Integer seq = CommonConstants.INTEGER_ONE;

        for (DIM020601BO bo : importList) {

           // 设置error信息
            setErrorInfo(form
                       , siteId
                       , seen
                       , facilityMap
                       , locationMap
                       , productMap
                       , bo);

            if(facilityMap.containsKey(bo.getPointCd()) && form.getPointId().equals(facilityMap.get(bo.getPointCd()).getFacilityId())) {
                bo.setPointId(form.getPointId());
            }
            if(locationMap.containsKey(bo.getLocationCd())) {
                bo.setLocationId(locationMap.get(bo.getLocationCd()).getLocationId());
            }
            if(productMap.containsKey(bo.getPartsNo())) {
                bo.setProductId(productMap.get(bo.getPartsNo()).getProductId());
                bo.setProductNm(productMap.get(bo.getPartsNo()).getSalesDescription());
            }
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
            bo.setSeq(seq);
            seq++;
        }

        return form;
    }

    private void setPointCode(DIM020601Form form) {

        form.setPointCd(dim0206Service.getFacilityById(form.getPointId()).getFacilityCd());
    }

    private void fromatForDB(List<DIM020601BO> importList) {

        for (DIM020601BO formatBo : importList) {

            formatBo.setDealerCd(formatStr(formatBo.getDealerCd()));
            formatBo.setPointCd(formatStr(formatBo.getPointCd()));
            formatBo.setLocationCd(formatStr(formatBo.getLocationCd()));
            formatBo.setPartsNo(PartNoUtil.formaForDB(formatStr(formatBo.getPartsNo())));
        }
    }

    private String formatStr(String code) {
        return StringUtils.isNotBlank(code) ? code.trim().toUpperCase() : CommonConstants.CHAR_BLANK;
    }

    private void setErrorInfo(DIM020601Form form
                                    , String siteId
                                    , Set<String> seen
                                    , Map<String, MstFacilityVO> facilityMap
                                    , Map<String, LocationVO> locationMap
                                    , Map<String, MstProductVO> productMap
                                    , DIM020601BO bo) {

        StringBuilder errorMsg = new StringBuilder();
        List<String> error          = new ArrayList<>();

        // check1: 如果Dealer Code为空或者与UC不一致
        if(StringUtils.isBlank(bo.getDealerCd()) || !siteId.equals(bo.getDealerCd())) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                                        , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                                                                     , bo.getDealerCd()
                                                                     , CodedMessageUtils.getMessage("label.tableCmmSiteMst")}));
        }

        // check2: Point Code 的id <> 画面.Point ID
        if(StringUtils.isBlank(bo.getPointCd())
                || !facilityMap.containsKey(bo.getPointCd())
                || (!form.getPointId().equals(facilityMap.get(bo.getPointCd()).getFacilityId()))) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.10410"
                                                        , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                                                     , bo.getPointCd()
                                                                     , CodedMessageUtils.getMessage("label.pointCode")
                                                                     , form.getPointCd()}));

        }

        // check3: Parts Code not exist!
        if(StringUtils.isBlank(bo.getPartsNo()) || !productMap.containsKey(bo.getPartsNo())) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                                        , new Object[]{CodedMessageUtils.getMessage("label.partsNo")
                                                                     , bo.getPartsNo()
                                                                     , CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        // check4: Location Code 为空 或 不在db中
        if(StringUtils.isBlank(bo.getLocationCd()) || !locationMap.containsKey(bo.getLocationCd())) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                                                         , new Object[]{CodedMessageUtils.getMessage("label.locationCode")
                                                         , bo.getLocationCd()
                                                         , CodedMessageUtils.getMessage("label.tableLocationInfo")}));
        }

        // check5: Qty 小于0
        if(BigDecimal.ZERO.compareTo(bo.getQty()) >= 0) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00200", new Object[]{CodedMessageUtils.getMessage("label.quantity"),CommonConstants.CHAR_ZERO}));
        }

        // check6: Average Cost 小于0
        if(BigDecimal.ZERO.compareTo(bo.getAverageCost()) >= 0) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00200", new Object[]{CodedMessageUtils.getMessage("label.averageCost"),CommonConstants.CHAR_ZERO}));
        }

        // check7: 导入数据Point Code 与 Location Code 与 Product Code 组合存在重复数据
        String key = bo.getPointCd() + "-" + bo.getLocationCd()+ "-" + bo.getPartsNo();
        if(!seen.add(key)) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00306"
                     , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                  , bo.getPointCd()
                                  , CodedMessageUtils.getMessage("label.locationCode")
                                  , bo.getLocationCd()
                                  , CodedMessageUtils.getMessage("label.partsNo")
                                  , bo.getPartsNo()}));
        }

        bo.setErrorMessage(errorMsg.toString());
        if(StringUtils.isNotBlank(errorMsg.toString())){
            error.add(errorMsg.toString());
        }
        bo.setError(error);

        if(!form.getErrorExistFlag() && StringUtils.isNotBlank(bo.getErrorMessage())) {
            form.setErrorExistFlag(true);
        }

    }

    private void checkStockTaking(Long facilityId, String siteId) {

        SystemParameterVO sysParamUpdate = dim0206Service.getProcessingSystemParameter(siteId, facilityId, MstCodeConstants.SystemParameterType.STOCKTAKING, CommonConstants.CHAR_ONE);
        if(sysParamUpdate == null) {// 如果没在盘点中，提示信息

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }
    }

    private void checkOrder(Long facilityId, String siteId) {

        Integer count1 = dim0206Service.countSalesOrder(facilityId, siteId);
        Integer count2 = dim0206Service.countServiceOrder(facilityId, siteId);
        Integer count3 = dim0206Service.countPartsStoring(facilityId, siteId);
        if(count1 > 0 || count2 > 0 || count3 > 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10289"));
        }
    }

    public void doConfirm(DIM020601Form form, String siteId) {

        checkStockTaking(form.getPointId(), siteId);

        updateOrInsert(form, siteId);
    }

    private void updateOrInsert(DIM020601Form form, String siteId) {

        // 画面上的明细
        List<DIM020601BO> detaiList = form.getGridDataList();
        Long facilityId = form.getPointId();

        if(detaiList.isEmpty()) {
            return;
        }

        // 转大写 去除前后空格
        fromatForDB(detaiList);

        // 准备数据
        List<ProductInventoryVO> proInvUpdateList = new ArrayList<>();
        Map<String, ProductStockStatusVO> stockStatusMap = new HashMap<>();
        List<ProductCostVO> proCostUpdateList = new ArrayList<>();
        Set<Long> seen = new HashSet<>();
        Set<String> statusTypes = paramStockStatusTypeIn();
        Set<Long> locationIds = detaiList.stream().map(DIM020601BO::getLocationId).collect(Collectors.toSet());
        Set<Long> productIds = detaiList.stream().map(DIM020601BO::getProductId).collect(Collectors.toSet());
        Map<Long, String> locTypeMap = dim0206Service.getlocTypeByIds(locationIds);
        List<ProductCostVO> proCostVOs = dim0206Service.getProCostVos(siteId, productIds);
        Map<Long, ProductCostVO> proCostMap = proCostVOs.stream().collect(Collectors.toMap(vo -> vo.getProductId(), vo -> vo));

        // delete backup
        List<ProductStockStatusBackupVO> stockStatusBkDel = dim0206Service.getStockStatusBK(siteId, facilityId);

        // insert or update ProductInventory准备数据
        List<ProductInventoryVO> proInvVos = dim0206Service.getProInvVOs(siteId, facilityId);
        proInvVos.stream()
                 .filter(vo -> BigDecimal.ZERO.compareTo(vo.getQuantity()) < 0)
                 .forEach(vo -> vo.setQuantity(BigDecimal.ZERO));
        Map<String, ProductInventoryVO> proInvMap = proInvVos.stream()
                                                             .filter(vo -> locationIds.contains(vo.getLocationId()) && productIds.contains(vo.getProductId()))
                                                             .collect(Collectors.toMap(
                                                                     vo -> vo.getLocationId().toString()
                                                                           + CommonConstants.CHAR_VERTICAL_BAR
                                                                           + vo.getProductId().toString(),
                                                                     vo -> vo
                                                             ));

        // delete ProductStockStatus
        List<ProductStockStatusVO> pStatusDel = dim0206Service.getProStatusVOs(siteId, facilityId);

        for(DIM020601BO bo : detaiList) {

         // insert or update ProductInventory
            generateProInv(siteId, proInvUpdateList, proInvMap, bo);

         // insert or update stockStatus
            generateProStockStatus(siteId, stockStatusMap, locTypeMap, bo);

         // insert or update productCost
            generateProCost(siteId, proCostUpdateList, proCostMap, bo, seen);
        }

        List<ProductStockStatusVO> proStatusUpdateList = new ArrayList<>(stockStatusMap.values());

        Set<Long> productInventoryIds = proInvUpdateList.stream()
                                                         .map(ProductInventoryVO::getProductInventoryId)
                                                         .collect(Collectors.toSet());

        List<ProductInventoryVO> filteredProInvVos = proInvVos.stream().filter(product -> !productInventoryIds.contains(product.getProductInventoryId())).collect(Collectors.toList());

        dim0206Service.saveInDB(siteId
                              , facilityId
                              , statusTypes
                              , stockStatusBkDel
                              , pStatusDel
                              , proInvUpdateList
                              , proStatusUpdateList
                              , proCostUpdateList
                              , filteredProInvVos
                              );
    }

    private void generateProCost(String siteId, List<ProductCostVO> proCostUpdateList, Map<Long, ProductCostVO> proCostMap, DIM020601BO bo, Set<Long> seen) {

        if(seen.add(bo.getProductId())) {//导入数据的product无重复

            if(proCostMap.containsKey(bo.getProductId())) {// update

                ProductCostVO productCostVO = proCostMap.get(bo.getProductId());
                productCostVO.setCost(bo.getAverageCost());
                proCostUpdateList.add(productCostVO);
            }else {// insert

                ProductCostVO productCostVO = new ProductCostVO();
                productCostVO.setSiteId(siteId);
                productCostVO.setProductId(bo.getProductId());
                productCostVO.setProductCd(bo.getPartsNo());
                productCostVO.setProductNm(bo.getProductNm());
                productCostVO.setCost(bo.getAverageCost());
                productCostVO.setCostType(CostType.AVERAGE_COST);
                productCostVO.setProductClassification(ProductClsType.PART.getCodeDbid());
                proCostUpdateList.add(productCostVO);
            }
        }
    }

    private void generateProStockStatus(String siteId, Map<String, ProductStockStatusVO> stockStatusMap, Map<Long, String> locTypeMap, DIM020601BO bo) {

        String key = bo.getPointId().toString()
                    + CommonConstants.CHAR_VERTICAL_BAR
                    + bo.getProductId().toString()
                    + CommonConstants.CHAR_VERTICAL_BAR
                    + locTypeMap.get(bo.getLocationId());

        if(stockStatusMap.containsKey(key)) {// import的数据key重复，数量累加

            ProductStockStatusVO statusUpdate = stockStatusMap.get(key);
            statusUpdate.setQuantity(bo.getQty().add(stockStatusMap.get(key).getQuantity()));
        }else {

            ProductStockStatusVO statusInsert = new ProductStockStatusVO();
            statusInsert.setSiteId(siteId);
            statusInsert.setFacilityId(bo.getPointId());
            statusInsert.setProductId(bo.getProductId());
            statusInsert.setProductStockStatusType(locTypeMap.get(bo.getLocationId()));
            statusInsert.setQuantity(bo.getQty());
            statusInsert.setProductClassification(ProductClsType.PART.getCodeDbid());
            stockStatusMap.put(key, statusInsert);
        }
    }

    private void generateProInv(String siteId, List<ProductInventoryVO> proInvUpdateList, Map<String, ProductInventoryVO> proInvMap, DIM020601BO bo) {

        String key = bo.getLocationId().toString()
                    + CommonConstants.CHAR_VERTICAL_BAR
                    + bo.getProductId().toString();

        if(proInvMap.containsKey(key)) {// update

            ProductInventoryVO proInvUpdate = proInvMap.get(key);
            proInvUpdate.setQuantity(proInvUpdate.getQuantity().add(bo.getQty()));
            proInvUpdateList.add(proInvUpdate);
        }else {// insert

            ProductInventoryVO proInvUpdate = new ProductInventoryVO();
            proInvUpdate.setSiteId(siteId);
            proInvUpdate.setFacilityId(bo.getPointId());
            proInvUpdate.setProductId(bo.getProductId());
            proInvUpdate.setQuantity(bo.getQty());
            proInvUpdate.setLocationId(bo.getLocationId());
            proInvUpdate.setPrimaryFlag(CommonConstants.CHAR_N);
            proInvUpdate.setProductClassification(ProductClsType.PART.getCodeDbid());
            proInvUpdateList.add(proInvUpdate);
            proInvMap.put(key, proInvUpdate);
        }
    }

    private Set<String> paramStockStatusTypeIn() {

        return new HashSet<>(Arrays.asList(SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid()         // S018EOONPURCHASEQTY
                                          ,SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid()         // S018ROONPURCHASEQTY
                                          ,SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid()         // S018HOONPURCHASEQTY
                                          ,SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid()         // S018WOONPURCHASEQTY
                                ));
    }

    private Map<String, MstProductVO> getProductMap(String siteId, List<DIM020601BO> detaiList) {

        Map<String, MstProductVO> vomap = new HashMap<>();
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);
        Set<String> productCds = detaiList.stream().map(DIM020601BO::getPartsNo).collect(Collectors.toSet());

        if(!siteList.isEmpty() && !productCds.isEmpty()) {
            vomap = dim0206Service.getProductByCds(productCds, siteList).stream().collect(Collectors.toMap(v -> v.getProductCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstFacilityVO> getFacilityMap(String siteId, List<DIM020601BO> detaiList) {

        Map<String, MstFacilityVO> vomap = new HashMap<>();
        Set<String> facilityCds = detaiList.stream().map(DIM020601BO::getPointCd).collect(Collectors.toSet());
        if(!facilityCds.isEmpty()) {
            vomap = dim0206Service.getFacilityByCd(facilityCds, siteId).stream().collect(Collectors.toMap(v -> v.getFacilityCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, LocationVO> getLocationMap(String siteId, Long facilityId, List<DIM020601BO> detaiList) {

        Map<String, LocationVO> vomap = new HashMap<>();
        Set<String> locationCds = detaiList.stream().map(DIM020601BO::getLocationCd).collect(Collectors.toSet());
        if(!locationCds.isEmpty()) {
            vomap = dim0206Service.getLocByCds(siteId, facilityId, locationCds).stream().collect(Collectors.toMap(v -> v.getLocationCd(),Function.identity()));
        }
        return vomap;
    }

    private Boolean checkStockLine(String siteId, Long facilityId) {

        return  dim0206Service.countStockLine(siteId, facilityId) > 0;
    }
}
