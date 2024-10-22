package com.a1stream.master.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.model.BaseTableData;
import com.a1stream.common.utils.ComUtil;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.master.CMM060201BO;
import com.a1stream.domain.bo.master.CMM060202BO;
import com.a1stream.domain.bo.master.CMM060202Detail;
import com.a1stream.domain.form.master.CMM060201Form;
import com.a1stream.domain.form.master.CMM060202Form;
import com.a1stream.domain.vo.ServicePackageCategoryVO;
import com.a1stream.domain.vo.ServicePackageItemVO;
import com.a1stream.domain.vo.ServicePackageVO;
import com.a1stream.master.service.CMM0602Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:
*
* @author mid1966
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.1    2024/10/08   SC              Modify
*/
@Component
public class CMM0602Facade {

    @Resource
    private CMM0602Service cmm0602Service;

    private final String PART = ProductClsType.PART.getCodeDbid();
    private final String SERVICE = ProductClsType.SERVICE.getCodeDbid();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    public List<CMM060201BO> getSvPackageData(CMM060201Form model, boolean isExport) {

        if(!StringUtils.isEmpty(model.getPackageNo()) && StringUtils.isEmpty(model.getPackageCd())) {

            throw new BusinessCodedException(ComUtil.t("M.E.00303", new String[] {ComUtil.t("label.packageNumber"), model.getPackageNo(), ComUtil.t("label.tableProductPackage")}));
        }

        List<CMM060201BO> resultList = cmm0602Service.getSvPackageData(model);

        if (isExport) {
            for(CMM060201BO bo : resultList) {
                if (bo.getValidDateFrom() != null) {
                    bo.setValidDateFrom(LocalDate.parse(bo.getValidDateFrom(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
                }
                if (bo.getValidDateTo() != null) {
                    bo.setValidDateTo(LocalDate.parse(bo.getValidDateTo(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
                }
            }
        }

        return resultList;
    }

    public CMM060202BO initSvPackageInfo(CMM060202Form form, PJUserDetails uc) {

        CMM060202BO result = new CMM060202BO();

        Long servicePackageId = form.getPackageInfo().getServicePackageId();
        if (servicePackageId == null) return result;

        String siteId = uc.getDealerCode();

        ServicePackageVO packageHeader = cmm0602Service.findServicePackage(servicePackageId);

        List<CMM060202Detail> categoryDetails = cmm0602Service.getCategoryDetails(servicePackageId, siteId);

        Map<String, List<CMM060202Detail>> svPackageItemGroup = cmm0602Service.getSvPackageDetailMap(servicePackageId, siteId);
        List<CMM060202Detail> serviceDetails = new ArrayList<>();
        List<CMM060202Detail> partsDetails = new ArrayList<>();
        if (svPackageItemGroup.containsKey(SERVICE)) {
            serviceDetails = svPackageItemGroup.get(SERVICE).stream().collect(Collectors.toList());
        }
        if (svPackageItemGroup.containsKey(PART)) {
            partsDetails = svPackageItemGroup.get(PART).stream().collect(Collectors.toList());
        }

        result = BeanMapUtils.mapTo(packageHeader, CMM060202BO.class);
        result.setCategoryDetails(categoryDetails);
        result.setServiceDetails(serviceDetails);
        result.setPartsDetails(partsDetails);

        return result;
    }

    public Long saveSvPackageInfo(CMM060202Form form, PJUserDetails uc) {

        String siteId = uc.getDealerCode();

        CMM060202BO packageInfo = form.getPackageInfo();
        BaseTableData<CMM060202Detail> categoryDetails = form.getCategoryDetails();
        BaseTableData<CMM060202Detail> serviceDetails = form.getServiceDetails();
        BaseTableData<CMM060202Detail> partsDetails = form.getPartsDetails();

        validBeforeSave(packageInfo, categoryDetails, serviceDetails, partsDetails, siteId);

        Map<Long, ServicePackageCategoryVO> svPackageCtgMap = new HashMap<>();
        Map<Long, ServicePackageItemVO> serviceItemMap = new HashMap<>();
        Map<Long, ServicePackageItemVO> partsItemMap = new HashMap<>();

        Long servicePackageId = packageInfo.getServicePackageId();
        if (servicePackageId != null) {

            svPackageCtgMap = cmm0602Service.getSvPackageCtgMap(siteId, servicePackageId);
            Map<String, List<ServicePackageItemVO>> svPackageItemGroup = cmm0602Service.getSvPackageItemMap(servicePackageId, siteId);
            if (svPackageItemGroup.containsKey(SERVICE)) {
                serviceItemMap = svPackageItemGroup.get(SERVICE).stream().collect(Collectors.toMap(ServicePackageItemVO::getServicePackageItemId, Function.identity()));;
            }
            if (svPackageItemGroup.containsKey(PART)) {
                partsItemMap = svPackageItemGroup.get(PART).stream().collect(Collectors.toMap(ServicePackageItemVO::getServicePackageItemId, Function.identity()));;
            }
        }
        // service_package Insert
        ServicePackageVO svPackage = buildSvPackage(packageInfo, siteId);

        servicePackageId = svPackage.getServicePackageId();
        // service_package_category
        List<ServicePackageCategoryVO> svPackageCtgList = buildSvPackageCtg(categoryDetails, svPackage, svPackageCtgMap, siteId);
        List<Long> removeSvPackageCtgId = categoryDetails.getRemoveRecords().stream().map(CMM060202Detail::getSvPackageCtgId).toList();

        // service_package_item Service
        List<Long> removeSvPackageItemId = new ArrayList<>();
        List<ServicePackageItemVO> svPackageItemList = buildSvPackageItem(serviceDetails, serviceItemMap, servicePackageId, SERVICE, siteId);
        List<Long> removeServiceItems = serviceDetails.getRemoveRecords().stream().map(CMM060202Detail::getPackageItemId).toList();
        if (!removeServiceItems.isEmpty()) {
            removeSvPackageItemId.addAll(removeServiceItems);
        }

        // service_package_item Parts
        svPackageItemList.addAll(buildSvPackageItem(partsDetails, partsItemMap, servicePackageId, PART, siteId));
        List<Long> removePartsItems = partsDetails.getRemoveRecords().stream().map(CMM060202Detail::getPackageItemId).toList();
        if (!removePartsItems.isEmpty()) {
            removeSvPackageItemId.addAll(removePartsItems);
        }

        cmm0602Service.maintainData(svPackage, svPackageCtgList, removeSvPackageCtgId, svPackageItemList, removeSvPackageItemId);

        return servicePackageId;
    }

    private List<ServicePackageItemVO> buildSvPackageItem(BaseTableData<CMM060202Detail> packageItems
                                                        , Map<Long, ServicePackageItemVO> svPackageItemMap
                                                        , Long servicePackageId
                                                        , String productClsType
                                                        , String siteId) {

        boolean isParts = StringUtils.equals(productClsType, PART);

        List<ServicePackageItemVO> svPackageItemList = new ArrayList<>();

        ServicePackageItemVO svPackageItem;
        for(CMM060202Detail item : packageItems.getNewUpdateRecords()) {

            if (!svPackageItemMap.containsKey(item.getPackageItemId())) {
                svPackageItem = new ServicePackageItemVO();
                svPackageItem.setSiteId(siteId);
                svPackageItem.setServicePackageId(servicePackageId);
                svPackageItem.setProductClassification(isParts? PART : SERVICE);
            } else {
                svPackageItem = svPackageItemMap.get(item.getPackageItemId());
            }
            if (isParts) {
                svPackageItem.setQty(item.getQty());
            }
            svPackageItem.setProductId(item.getProductId());

            svPackageItemList.add(svPackageItem);
        }

        return svPackageItemList;
    }

    private List<ServicePackageCategoryVO> buildSvPackageCtg(BaseTableData<CMM060202Detail> categoryDetails
                                                            , ServicePackageVO svPackage
                                                            , Map<Long, ServicePackageCategoryVO> dbSvPackageCtgMap
                                                            , String siteId) {

        List<ServicePackageCategoryVO> svPackageCtgList = new ArrayList<>();

        ServicePackageCategoryVO svPackageCtg;
        for(CMM060202Detail category : categoryDetails.getNewUpdateRecords()) {

            if (dbSvPackageCtgMap.containsKey(category.getSvPackageCtgId())) {
                svPackageCtg = dbSvPackageCtgMap.get(category.getSvPackageCtgId());
            } else {
                svPackageCtg = new ServicePackageCategoryVO();
                svPackageCtg.setSiteId(siteId);
                svPackageCtg.setServicePackageId(svPackage.getServicePackageId());
            }

            svPackageCtg.setProductCategoryId(category.getCategoryId());
            svPackageCtg.setFromDate(svPackage.getFromDate());
            svPackageCtg.setToDate(svPackage.getToDate());

            svPackageCtgList.add(svPackageCtg);
        }

        return svPackageCtgList;
    }

    private ServicePackageVO buildSvPackage(CMM060202BO packageInfo, String siteId) {

        ServicePackageVO svPackage;
        if (packageInfo.getServicePackageId() == null) {
            svPackage = ServicePackageVO.create();
        } else {
            svPackage = cmm0602Service.findServicePackage(packageInfo.getServicePackageId());
        }
        svPackage.setSiteId(siteId);
        svPackage.setPackageCd(packageInfo.getPackageCd());
        svPackage.setLocalDescription(packageInfo.getLocalDescription());
        svPackage.setSalesDescription(packageInfo.getSalesDescription());
        svPackage.setEnglishDescription(packageInfo.getEnglishDescription());
        svPackage.setFromDate(packageInfo.getFromDate());
        svPackage.setToDate(packageInfo.getToDate());
        svPackage.setServiceCategory(packageInfo.getServiceCategory());

        return svPackage;
    }

    private void validBeforeSave(CMM060202BO packageInfo
                                , BaseTableData<CMM060202Detail> categoryDetails
                                , BaseTableData<CMM060202Detail> serviceDetails
                                , BaseTableData<CMM060202Detail> partsDetails
                                , String siteId) {

        // 1.新增的场合，如果packageNo已存在，则报错
        if(packageInfo.getServicePackageId() == null) {

            int isExistPackageCd = cmm0602Service.existServicePackage(packageInfo.getPackageCd(), siteId);
            if(isExistPackageCd > 0) {
                throw new BusinessCodedException(ComUtil.t("M.E.00309", new String[] {ComUtil.t("label.packageNumber"), packageInfo.getPackageCd(), ComUtil.t("label.servicePackage")}));
            }
        }

        List<CMM060202Detail> categoryList = categoryDetails.getNewUpdateRecords();
        List<CMM060202Detail> serviceList = serviceDetails.getNewUpdateRecords();
        List<CMM060202Detail> partsList = partsDetails.getNewUpdateRecords();
        // 验证productCategory不得重复
        Optional<String> dupCategory = categoryList.stream().filter(service -> StringUtils.isNotBlank(service.getCategoryCd()))
                                                                .map(CMM060202Detail::getCategoryCd)
                                                                .collect(Collectors.groupingBy(newCategory -> newCategory, Collectors.counting()))
                                                                .entrySet().stream()
                                                                .filter(entry -> entry.getValue() > 1)
                                                                .map(Map.Entry::getKey).findFirst();
        if (dupCategory.isPresent()) {
            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.productCategory"), dupCategory.get()}));
        }
        // 验证productCategory的正确性
        List<CMM060202Detail> invalidCategory = categoryDetails.getNewUpdateRecords().stream().filter(category -> category.getCategoryId() == null).toList();
        if(!invalidCategory.isEmpty()) {
            CMM060202Detail invalidRow = invalidCategory.get(0);
            throw new BusinessCodedException(ComUtil.t("M.E.00340", new String[] {ComUtil.t("label.productCategory"), invalidRow.getCategoryCd() }));
        }

        // 验证serviceJob不得重复
        Optional<String> dupService = serviceList.stream().filter(service -> StringUtils.isNotBlank(service.getProductCd()))
                                                               .map(CMM060202Detail::getProductCd)
                                                               .collect(Collectors.groupingBy(newServiceJob -> newServiceJob, Collectors.counting()))
                                                               .entrySet().stream()
                                                               .filter(entry -> entry.getValue() > 1)
                                                               .map(Map.Entry::getKey).findFirst();
        if (dupService.isPresent()) {
            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.serviceJob"), dupService.get()}));
        }
        // 验证serviceJob的正确性
        List<CMM060202Detail> invalidService = serviceList.stream().filter(service -> service.getProductId() == null).toList();
        if(!invalidService.isEmpty()) {
            CMM060202Detail invalidRow = invalidService.get(0);
            throw new BusinessCodedException(ComUtil.t("M.E.00340", new String[] {ComUtil.t("label.serviceJob"), invalidRow.getProductCd() }));
        }

        // 验证partsNo不得重复
        Optional<String> dupParts = partsList.stream().filter(parts -> StringUtils.isNotBlank(parts.getProductCd()))
                                                          .map(CMM060202Detail::getProductCd)
                                                          .collect(Collectors.groupingBy(newParts -> newParts, Collectors.counting()))
                                                          .entrySet().stream()
                                                          .filter(entry -> entry.getValue() > 1)
                                                          .map(Map.Entry::getKey).findFirst();
        if (dupParts.isPresent()) {
            throw new BusinessCodedException(ComUtil.t("M.E.00304", new String[] {ComUtil.t("label.partsNumber"), PartNoUtil.format(dupParts.get())}));
        }
        // 验证partsNo的正确性
        List<CMM060202Detail> invalidParts = partsList.stream().filter(service -> service.getProductId() == null).toList();
        if(!invalidParts.isEmpty()) {
            CMM060202Detail invalidRow = invalidParts.get(0);
            throw new BusinessCodedException(ComUtil.t("M.E.00340", new String[] {ComUtil.t("label.partsNumber"), invalidRow.getProductCd() }));
        }
    }
}