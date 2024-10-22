package com.a1stream.parts.facade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants.LocationType;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.parts.DIM020701BO;
import com.a1stream.domain.form.parts.DIM020701Form;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.domain.vo.MstCodeInfoVO;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductInventoryVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.a1stream.domain.vo.WorkzoneVO;
import com.a1stream.parts.service.DIM0207Service;
import com.alibaba.excel.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:Location Import
*
* @author mid2178
*/
@Component
public class DIM0207Facade {

    @Resource
    private DIM0207Service dim0207Service;

    public DIM020701Form checkFile(DIM020701Form form, String siteId) {

        // 根据条件部的point,如果未处于盘点中，提示信息
        checkStockTaking(form.getPointId(), siteId);

        // 根据条件部的point,校验完成情况
        checkOrder(form.getPointId(), siteId);

        // 将导入的数据和error信息设置到画面上
        return setSreenList(form, siteId, form.getPointId());
    }

    private DIM020701Form setSreenList(DIM020701Form form, String siteId,Long facilityId) {

        List<DIM020701BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        // 如果在product_inventory存在数据，confirm前在画面弹提示
        form.setCheckFlag(checkStockLine(siteId, form.getPointId()));

        setPointCode(form);

        // 转大写 去除前后空格
        fromatForDB(importList);

        Set<String> seen = new HashSet<>();
        Map<String, MstFacilityVO> facilityMap = getFacilityMap(siteId, importList);
        Map<String, MstProductVO> productMap = getProductMap(siteId, importList);
        Map<String, MstCodeInfoVO> locationTypeMap = getLocationTypeMap(importList);
        Map<String, BinTypeVO> binTypeMap = getBinTypeMap(siteId, importList);
        Map<String, WorkzoneVO> workzoneMap = getWzMap(siteId, facilityId ,importList);

        Integer seq = CommonConstants.INTEGER_ONE;

        for (DIM020701BO bo : importList) {

           // 设置error信息
            setErrorInfo(form
                       , siteId
                       , seen
                       , facilityMap
                       , productMap
                       , locationTypeMap
                       , binTypeMap
                       , workzoneMap
                       , bo);

            if(facilityMap.containsKey(bo.getPointCd()) && form.getPointId().equals(facilityMap.get(bo.getPointCd()).getFacilityId())) {
                bo.setPointId(form.getPointId());
            }
            if(productMap.containsKey(bo.getPartsNo())) {
                bo.setProductId(productMap.get(bo.getPartsNo()).getProductId());
            }
            if(locationTypeMap.containsKey(bo.getLocationType())) {
                bo.setLocationTypeId(locationTypeMap.get(bo.getLocationType()).getCodeDbid());
            }
            if(binTypeMap.containsKey(bo.getBinType())) {
                bo.setBinTypeId(binTypeMap.get(bo.getBinType()).getBinTypeId());
            }
            if(workzoneMap.containsKey(bo.getWorkzoneCd())) {
                bo.setWorkzoneId(workzoneMap.get(bo.getWorkzoneCd()).getWorkzoneId());
            }
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
            bo.setSeq(seq);
            seq++;
        }

        return form;
    }

    private void setPointCode(DIM020701Form form) {

        form.setPointCd(dim0207Service.getFacilityById(form.getPointId()).getFacilityCd());
    }

    private void fromatForDB(List<DIM020701BO> importList) {

        for (DIM020701BO formatBo : importList) {

            formatBo.setDealerCd(formatStr(formatBo.getDealerCd()));
            formatBo.setPointCd(formatStr(formatBo.getPointCd()));
            formatBo.setWorkzoneCd(formatStr(formatBo.getWorkzoneCd()));
            formatBo.setLocationCd(formatStr(formatBo.getLocationCd()));
            formatBo.setLocationType(formatStr(formatBo.getLocationType()));
            formatBo.setPartsNo(PartNoUtil.formaForDB(formatStr(formatBo.getPartsNo())));
        }
    }

    private String formatStr(String code) {
        return StringUtils.isNotBlank(code) ? code.trim().toUpperCase() : CommonConstants.CHAR_BLANK;
    }

    private void setErrorInfo(DIM020701Form form
                                    , String siteId
                                    , Set<String> seen
                                    , Map<String, MstFacilityVO> facilityMap
                                    , Map<String, MstProductVO> productMap
                                    , Map<String, MstCodeInfoVO> locationTypeMap
                                    , Map<String, BinTypeVO> binTypeMap
                                    , Map<String, WorkzoneVO> workzoneMap
                                    , DIM020701BO bo) {

        List<String> error = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        String formatPartNo = PartNoUtil.format(bo.getPartsNo());

        // check1: 如果Dealer Code为空或者与UC不一致
        if(StringUtils.isBlank(bo.getDealerCd()) || !siteId.equals(bo.getDealerCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                    , new Object[]{CodedMessageUtils.getMessage("label.dealerCode")
                                 , bo.getDealerCd()
                                 , CodedMessageUtils.getMessage("label.tableCmmSiteMst")}));
        }

        // check2: Point Code 不存在mst_facility中
        if(StringUtils.isBlank(bo.getPointCd()) || !facilityMap.containsKey(bo.getPointCd())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                    , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                 , bo.getPointCd()
                                 , CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }else {

            if(!form.getPointId().equals(facilityMap.get(bo.getPointCd()).getFacilityId())) {

                // check3: Point Code 的id <> 画面.Point ID
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10410"
                        , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                     , bo.getPointCd()
                                     , CodedMessageUtils.getMessage("label.pointCode")
                                     , form.getPointId()}));
               }
        }

        // check4: 导入数据Point Code 与 Product Code 组合存在重复数据
        if(StringUtils.isNotBlank(bo.getPartsNo())){
            String key = bo.getPointCd() + "-" + bo.getPartsNo();
            if(!seen.add(key)) {

                 errorMsg.append(formatPartNo + CodedMessageUtils.getMessage("M.E.10411"));
            }
        }

        // check5: Product Code 不存在 mst_product中 -> 20240820 mid2259更新 当partsNo会空时不会触发异常，仅新增location表，product Inventory表不会新增
        if(StringUtils.isNotBlank(bo.getPartsNo()) && !productMap.containsKey(bo.getPartsNo())) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                        , new Object[]{CodedMessageUtils.getMessage("label.partsNo")
                                     , formatPartNo
                                     , CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        // check6: Location Type不存在 基础数据中
        if(StringUtils.isBlank(bo.getLocationType()) || !locationTypeMap.containsKey(bo.getLocationType())) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                    , new Object[]{CodedMessageUtils.getMessage("label.locationType")
                                 , bo.getLocationType()
                                 , CodedMessageUtils.getMessage("label.locationType")}));
        }else {

            if(productMap.containsKey(bo.getPartsNo())
                    && (LocationType.FROZEN.getCodeDbid().equals(locationTypeMap.get(bo.getLocationType()).getCodeDbid())
                    || LocationType.SERVICE.getCodeDbid().equals(locationTypeMap.get(bo.getLocationType()).getCodeDbid()))) {

             // check7: mst_product.product_id不为空 且locationType=FROZEN 或 =S006SERVICE
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10412"));
            }
        }

        // check8: Bin Type 不存在bin_type中
        if(StringUtils.isBlank(bo.getBinType()) || !binTypeMap.containsKey(bo.getBinType())) {

             errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                    , new Object[]{CodedMessageUtils.getMessage("label.binType")
                                 , bo.getBinType()
                                 , CodedMessageUtils.getMessage("label.binType")}));
        }

        // check9: Workzone Code 与 PointCode 组合不存在
        Long facilityId = facilityMap.containsKey(bo.getPointCd()) ? facilityMap.get(bo.getPointCd()).getFacilityId() : null;
        if(StringUtils.isBlank(bo.getWorkzoneCd())
                || !workzoneMap.containsKey(bo.getWorkzoneCd())
                || !Objects.equals(workzoneMap.get(bo.getWorkzoneCd()).getFacilityId(), facilityId)) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                    , new Object[]{CodedMessageUtils.getMessage("label.workZone")
                                 , bo.getWorkzoneCd()
                                 , CodedMessageUtils.getMessage("label.tableWorkZoneInfo")}));
        }

        // check10: Location Code 为空 或 长度大于15
        if(StringUtils.isBlank(bo.getLocationCd()) || bo.getLocationCd().length() > 15) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.10413"
                    , new Object[]{CodedMessageUtils.getMessage("label.locationCode")
                                 , CommonConstants.INTEGER_ONE
                                 , CommonConstants.INTEGER_FIFTEEN}));
        }

        // check11: 导入数据Point Code 与 Location Code 与 Product Code 组合存在重复数据
        String key2 = bo.getPointCd() + "-" + bo.getLocationCd()+ "-" + bo.getPartsNo();
        if(!seen.add(key2)) {

            errorMsg.append(CodedMessageUtils.getMessage("M.E.00306"
                    , new Object[]{CodedMessageUtils.getMessage("label.pointCode")
                                 , bo.getPointCd()
                                 , CodedMessageUtils.getMessage("label.locationCode")
                                 , bo.getLocationCd()
                                 , CodedMessageUtils.getMessage("label.partsNo")
                                 , formatPartNo}));
        }

        bo.setErrorMessage(errorMsg.toString());
        if(StringUtils.isNotBlank(errorMsg.toString())){
            error.add(errorMsg.toString());
        }
        bo.setError(error);

        if(Boolean.FALSE.equals(form.getErrorExistFlag()) && StringUtils.isNotBlank(bo.getErrorMessage())) {
            form.setErrorExistFlag(true);
        }
    }

    private void checkStockTaking(Long facilityId, String siteId) {

        SystemParameterVO sysParamUpdate = dim0207Service.getProcessingSystemParameter(siteId, facilityId, MstCodeConstants.SystemParameterType.STOCKTAKING, CommonConstants.CHAR_ONE);
        if(sysParamUpdate == null) {// 如果没在盘点中，提示信息

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.C.10143"));
        }
    }

    private void checkOrder(Long facilityId, String siteId) {

        Integer count1 = dim0207Service.countSalesOrder(facilityId, siteId);
        Integer count2 = dim0207Service.countServiceOrder(facilityId, siteId);
        Integer count3 = dim0207Service.countPartsStoring(facilityId, siteId);
        if(count1 > 0 || count2 > 0 || count3 > 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10289"));
        }
    }

    public void doConfirm(DIM020701Form form, String siteId) {

        checkStockTaking(form.getPointId(), siteId);

        updateOrInsert(form, siteId);
    }

    private void updateOrInsert(DIM020701Form form, String siteId) {

        // 画面上的明细
        List<DIM020701BO> detaiList = form.getGridDataList();

        if(detaiList.isEmpty()) {
            return;
        }

        // 转大写 去除前后空格
        fromatForDB(detaiList);
        List<LocationVO> locationList = new ArrayList<>();
        List<ProductInventoryVO> proInventoryList = new ArrayList<>();

        Long facilityId = form.getPointId();
        Set<String> locationCds = detaiList.stream().map(DIM020701BO::getLocationCd).collect(Collectors.toSet());
        Set<String> productCds = detaiList.stream().map(DIM020701BO::getPartsNo).collect(Collectors.toSet());
        Map<String, LocationVO> locationMap = getLocationMap(siteId, facilityId, locationCds);

        //需要将原本的主货位取消
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);

        Set<Long> productIds = dim0207Service.getProductByCds(productCds, siteList).stream().map(MstProductVO::getProductId).collect(Collectors.toSet());
        List<ProductInventoryVO> oldProInventoryList = dim0207Service.findMainProductInventoryList(siteId, productIds, facilityId, CommonConstants.CHAR_Y);
        Set<Long> beforeLocationIds = oldProInventoryList.stream().map(ProductInventoryVO::getLocationId).collect(Collectors.toSet());

        // 根据locationId+facilityId+productId看是否存在productInventory表中，facilityId=条件部pointId
        // key:locationId+|+productId
        Map<String, ProductInventoryVO> proInvMap = dim0207Service.getProInvByCds(locationCds, productCds, facilityId, siteId);

        //过滤oldProInventoryList,去掉本次需要设置为主货位的ProductInventory,过滤后将库位的主货位设置为N
        List<ProductInventoryVO> productInventoryList = proInvMap.values().stream().collect(Collectors.toList());
        Set<Long> productInventoryIds = productInventoryList.stream()
                                                              .map(ProductInventoryVO::getProductInventoryId)
                                                              .collect(Collectors.toSet());

        List<ProductInventoryVO> filteredOldProInventoryList = oldProInventoryList.stream()
                                                              .filter(item -> !productInventoryIds.contains(item.getProductInventoryId()))
                                                              .collect(Collectors.toList());

        filteredOldProInventoryList.forEach(item -> item.setPrimaryFlag(CommonConstants.CHAR_N));

        proInventoryList.addAll(filteredOldProInventoryList);

        for(DIM020701BO screenBO : detaiList) {

            // location 新增或者更新
            LocationVO locationVO = generateLocation(siteId, locationList, locationMap, screenBO);

            // product_inventory 新增或者更新
            generateProductInventory(siteId, proInventoryList, proInvMap, screenBO, locationVO);
        }

        dim0207Service.saveInDB(locationList, proInventoryList,beforeLocationIds);
    }

    private void generateProductInventory(String siteId
                                        , List<ProductInventoryVO> proInventoryList
                                        , Map<String, ProductInventoryVO> proInvMap
                                        , DIM020701BO screenBO
                                        , LocationVO locationVO) {

        if (!ObjectUtils.isEmpty(screenBO.getProductId())) {

            String key = locationVO.getLocationId().toString()
                       + CommonConstants.CHAR_VERTICAL_BAR
                       + screenBO.getProductId().toString();

            if(proInvMap.containsKey(key)) {

                ProductInventoryVO updateVo =  proInvMap.get(key);
                updateVo.setPrimaryFlag(CommonConstants.CHAR_Y);
                proInventoryList.add(updateVo);
            }else {

                ProductInventoryVO insertVo = new ProductInventoryVO();
                insertVo.setSiteId(siteId);
                insertVo.setFacilityId(screenBO.getPointId());
                insertVo.setProductId(screenBO.getProductId());
                insertVo.setPrimaryFlag(CommonConstants.CHAR_Y);
                insertVo.setProductClassification(ProductClsType.PART.getCodeDbid());
                insertVo.setLocationId(locationVO.getLocationId());
                proInventoryList.add(insertVo);
            }
        }
    }

    private LocationVO generateLocation(String siteId
                                      , List<LocationVO> locationList
                                      , Map<String, LocationVO> locationMap
                                      , DIM020701BO screenBO) {

        LocationVO locationVO;
        if(locationMap.containsKey(screenBO.getLocationCd())) {// update

            locationVO = locationMap.get(screenBO.getLocationCd());
            locationVO.setWorkzoneId(screenBO.getWorkzoneId());
            locationVO.setLocationType(screenBO.getLocationTypeId());
            locationVO.setBinTypeId(screenBO.getBinTypeId());
            if(StringUtils.isNotBlank(screenBO.getPartsNo())){
                locationVO.setPrimaryFlag(CommonConstants.CHAR_Y);
            }else{
                List<ProductInventoryVO> mainProductInventoryVOs = dim0207Service.getMainProductInventoryVOs(locationVO.getLocationId());
                if(mainProductInventoryVOs.isEmpty()){
                    locationVO.setPrimaryFlag(CommonConstants.CHAR_N);
                }else{
                    locationVO.setPrimaryFlag(CommonConstants.CHAR_Y);
                }
            }
            locationList.add(locationVO);

        }else {// insert

            locationVO = LocationVO.create();
            locationVO.setSiteId(siteId);
            locationVO.setFacilityId(screenBO.getPointId());
            locationVO.setWorkzoneId(screenBO.getWorkzoneId());
            locationVO.setLocationCd(screenBO.getLocationCd());
            locationVO.setLocationType(screenBO.getLocationTypeId());
            locationVO.setBinTypeId(screenBO.getBinTypeId());
            if(StringUtils.isNotBlank(screenBO.getPartsNo())){
                locationVO.setPrimaryFlag(CommonConstants.CHAR_Y);
            }else{
                locationVO.setPrimaryFlag(CommonConstants.CHAR_N);
            }
            locationList.add(locationVO);
        }
        return locationVO;
    }

    private Map<String, MstProductVO> getProductMap(String siteId, List<DIM020701BO> detaiList) {

        Map<String, MstProductVO> vomap = new HashMap<>();
        List<String> siteList = List.of(CommonConstants.CHAR_DEFAULT_SITE_ID, siteId);
        Set<String> productCds = detaiList.stream().map(DIM020701BO::getPartsNo).collect(Collectors.toSet());

        if(!siteList.isEmpty() && !productCds.isEmpty()) {
            vomap = dim0207Service.getProductByCds(productCds, siteList).stream().collect(Collectors.toMap(v -> v.getProductCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstFacilityVO> getFacilityMap(String siteId, List<DIM020701BO> detaiList) {

        Map<String, MstFacilityVO> vomap = new HashMap<>();
        Set<String> facilityCds = detaiList.stream().map(DIM020701BO::getPointCd).collect(Collectors.toSet());
        if(!facilityCds.isEmpty()) {
            vomap = dim0207Service.getFacilityByCd(facilityCds, siteId).stream().collect(Collectors.toMap(v -> v.getFacilityCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, WorkzoneVO> getWzMap(String siteId, Long facilityId,  List<DIM020701BO> detaiList) {

        Map<String, WorkzoneVO> vomap = new HashMap<>();
        Set<String> workzoneCds = detaiList.stream().map(DIM020701BO::getWorkzoneCd).collect(Collectors.toSet());

        if(!workzoneCds.isEmpty()) {
            vomap = dim0207Service.getWzByCds(workzoneCds, siteId,facilityId).stream().collect(Collectors.toMap(v -> v.getWorkzoneCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, BinTypeVO> getBinTypeMap(String siteId, List<DIM020701BO> detaiList) {

        Map<String, BinTypeVO> vomap = new HashMap<>();
        Set<String> binTypes = detaiList.stream().map(DIM020701BO::getBinType).collect(Collectors.toSet());
        if(!binTypes.isEmpty()) {
            vomap = dim0207Service.getBinTypeByDescription(binTypes, siteId).stream().collect(Collectors.toMap(v -> v.getDescription(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, LocationVO> getLocationMap(String siteId, Long facilityId, Set<String> locationCds) {

        Map<String, LocationVO> vomap = new HashMap<>();
        if(!locationCds.isEmpty()) {
            vomap = dim0207Service.getLocByCds(siteId, facilityId, locationCds).stream().collect(Collectors.toMap(v -> v.getLocationCd(),Function.identity()));
        }
        return vomap;
    }

    private Map<String, MstCodeInfoVO> getLocationTypeMap(List<DIM020701BO> detaiList) {

        Map<String, MstCodeInfoVO> vomap = new HashMap<>();
        Set<String> locationTypes = detaiList.stream().map(DIM020701BO::getLocationType).collect(Collectors.toSet());
        if(!locationTypes.isEmpty()) {
            vomap = dim0207Service.getLocationType(LocationType.CODE_ID ,locationTypes).stream().collect(Collectors.toMap(v -> v.getCodeData1(),Function.identity()));
        }
        return vomap;
    }

    private Boolean checkStockLine(String siteId, Long facilityId) {

        return  dim0207Service.countStockLine(siteId, facilityId) > 0;
    }
}
