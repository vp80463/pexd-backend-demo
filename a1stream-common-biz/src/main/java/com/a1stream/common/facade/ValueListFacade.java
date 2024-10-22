package com.a1stream.common.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ServiceCategory;
import com.a1stream.common.logic.ServiceLogic;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ConsumerVLForm;
import com.a1stream.common.model.DemandVLForm;
import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ServiceJobVLBO;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.SymptomVLBO;
import com.a1stream.common.model.ValueListResultBO;
import com.a1stream.common.service.ValueListService;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.common.DemandBO;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

@Component
public class ValueListFacade {

    @Resource
    private ValueListService valueListService;

    @Resource
    private ServiceLogic serviceLogic;

    public ValueListResultBO findUserRoleList(PJUserDetails uc, BaseVLForm model) {

        return valueListService.findUserRoleList(uc.getDealerCode(), model);
    }

    public ValueListResultBO findEmployeeList(BaseVLForm model, String siteId) {

        return valueListService.findEmployeeList(model, siteId);
    }

    public ValueListResultBO findPointList(PJUserDetails uc, BaseVLForm model) {

        return valueListService.findPointList(uc, model);
    }

    public ValueListResultBO findSupplierList(String siteId, BaseVLForm model) {

        return valueListService.findSupplierList(siteId, model);
    }

    public ValueListResultBO findDealerList(BaseVLForm model){

        return valueListService.findDealerList(model);
    }

    public ValueListResultBO findUserValueList(BaseVLForm model){

        return valueListService.findUserValueList(model);
    }

    public ValueListResultBO findModelList(BaseVLForm model){

        return valueListService.findModelList(model);
    }

    public ValueListResultBO findSdModelList(BaseVLForm model){

        return valueListService.findSdModelList(model);
    }

    public ValueListResultBO findPartsList(PartsVLForm model, String siteId, String taxRateFromDate) {

        setPartsQueryModel(model);

        ValueListResultBO result = valueListService.findPartsList(model, siteId);

        return setValue2PartsBO(result, model, siteId, taxRateFromDate, false);
    }

    public ValueListResultBO findYamahaPartsList(PartsVLForm model, String taxRateFromDate) {

        setPartsQueryModel(model);

        ValueListResultBO result = valueListService.findYamahaPartsList(model);

        return setValue2PartsBO(result, model, null, taxRateFromDate, true);
    }

    public ValueListResultBO findSectionList(BaseVLForm model){

        return valueListService.findSectionList(model);
    }

    public ValueListResultBO findPackageList(BaseVLForm model, String siteId){

        return valueListService.findPackageList(model, siteId);
    }

    public ValueListResultBO findDemandList(DemandVLForm model)  {

        List<DemandBO> list = new ArrayList<>();
        /**
         * arg0: serviceCategory
         * arg1：serializedProductId
         * arg2: soldDate
         * */
        if (StringUtils.isBlank(model.getArg0())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00117", new String[] { CodedMessageUtils.getMessage("label.serviceCategory")}));
        }
        //目前只有freeCoupon一种demand，如果不是，直接回滚
        if (!StringUtils.equals(model.getArg0(), ServiceCategory.FREECOUPON.getCodeDbid())) {
            return new ValueListResultBO(list);
        }
        if (StringUtils.isBlank(model.getArg1())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00117", new String[] { CodedMessageUtils.getMessage("label.model")}));
        }
        if (StringUtils.isBlank(model.getArg2())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00117", new String[] { CodedMessageUtils.getMessage("label.soldDate")}));
        }

        //优先判断大车型，根据车辆获取大车型的coupon数据
        list = valueListService.findSerializedProductList(model);

        //如果在大车型master中无数据，则走普通的serviceDemand.
        if (list.isEmpty()) {
            list=valueListService.findServiceDemandList(model);
        }

        String dateFormat = CommonConstants.DB_DATE_FORMAT_YMD;
        LocalDate baseDate = LocalDate.parse(model.getArg2(),DateTimeFormatter.ofPattern(dateFormat));

        int length =1;
        for (DemandBO demandBO : list) {

            if(!ObjectUtils.isEmpty(baseDate)) {
                LocalDate fromDate = baseDate.plusMonths(Integer.parseInt(demandBO.getBaseDateAfter()));
                demandBO.setFromDate(fromDate.format(DateTimeFormatter.ofPattern(dateFormat)));

                LocalDate toDate = fromDate.plusMonths(Integer.parseInt(demandBO.getDuePeriod()));
                toDate = toDate.minusDays(CommonConstants.INTEGER_ONE);
                demandBO.setToDate(toDate.format(DateTimeFormatter.ofPattern(dateFormat)));
            }

            //set jobCode
            if (length==CommonConstants.INTEGER_ONE) {
                demandBO.setJobCode(PJConstants.SpecialServiceDemandJobCodeType.KEY_FS03);
            }else if(length==CommonConstants.INTEGER_THREE || length==CommonConstants.INTEGER_SIX || length==CommonConstants.INTEGER_NINE){
                demandBO.setJobCode(PJConstants.SpecialServiceDemandJobCodeType.KEY_FS01);
            }else {
                demandBO.setJobCode(PJConstants.SpecialServiceDemandJobCodeType.KEY_FS02);
            }
            length++;
        }

        return new ValueListResultBO(list);
    }

    public ValueListResultBO findConsumerByUnitList(ConsumerVLForm model, String siteId){

        return valueListService.findConsumerByUnitList(model, siteId);
    }
    public ValueListResultBO findFaultCodeList(BaseVLForm model){

        return valueListService.findFaultCodeList(model);
    }

    public ValueListResultBO findFaultDescriptionList(BaseVLForm model){

        return valueListService.findFaultDescriptionList(model);
    }

    public ValueListResultBO findServiceJobList(BaseVLForm model){

        return valueListService.findServiceJobList(model);
    }

    public ValueListResultBO findServiceJobByModelList(ServiceJobVLForm model, String taxPeriod){

        Set<String> modeCodes = new HashSet<>();
        if (StringUtils.isNotBlank(model.getModelCode())) {
            String modelCodeStr = model.getModelCode();
            long modelCodeLength = StringUtils.trim(modelCodeStr).length();
            if (modelCodeLength <= 3){
                modeCodes.add(modelCodeStr);
            }
            String subModelCode = null;
            for (int i = 3; i <= modelCodeLength; i++) {
                subModelCode = StringUtils.trim(modelCodeStr).substring(0, i);
                modeCodes.add(subModelCode);
            }
        }

        ValueListResultBO result = valueListService.findServiceJobByModelList(model, modeCodes);

        List<ServiceJobVLBO> serviceJobList = (List<ServiceJobVLBO>) result.getList();

        String workingType = PJConstants.SettleType.CUSTOMER.getCodeDbid().equals(model.getSettleTypeId()) ? MstCodeConstants.SystemParameterType.STDWORKINGHOURPRICECUSTOMER : MstCodeConstants.SystemParameterType.STDWORKINGHOURPRICEFACTORY;
        SystemParameterVO systemParameterVo = valueListService.findSysParamaterById(workingType);
        BigDecimal stdRetailPrice = systemParameterVo == null ? BigDecimal.ZERO : new BigDecimal(systemParameterVo.getParameterValue());
        BigDecimal defaultTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.TAXRATE);

        // 系统时间 > 系统税率设置时间，使用S074JOBTAXRATE相应的税率计算价格。
        if(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).compareTo(taxPeriod) >= 0) {
            BigDecimal jobTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.JOBTAXRATE);
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(jobTaxRate);
                serviceJobVLBO.setStdRetailPrice(serviceLogic.calculateStdRetailPriceForJob(new BigDecimal(serviceJobVLBO.getManHours()).multiply(stdRetailPrice), jobTaxRate, defaultTaxRate));
            }
        }else {
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(defaultTaxRate);
                serviceJobVLBO.setStdRetailPrice(new BigDecimal(serviceJobVLBO.getManHours()).multiply(stdRetailPrice));
            }
        }

        return new ValueListResultBO(serviceJobList, result.getTotal());
    }

    public ValueListResultBO findServiceJobByModelTypeList(ServiceJobVLForm model, String taxPeriod) {

        ValueListResultBO result = valueListService.findServiceJobByModelTypeList(model);

        List<ServiceJobVLBO> serviceJobList = (List<ServiceJobVLBO>) result.getList();
        BigDecimal defaultTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.TAXRATE);

        //系统时间 > 系统税率设置时间，使用S074JOBTAXRATE相应的税率计算价格。
        if(LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD)).compareTo(taxPeriod) >= 0) {
            BigDecimal jobTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.JOBTAXRATE);
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(jobTaxRate);
                serviceJobVLBO.setStdRetailPrice(serviceLogic.calculateStdRetailPriceForJob(serviceJobVLBO.getStdRetailPrice(), jobTaxRate, defaultTaxRate));
            }
        }
        else {
            for (ServiceJobVLBO serviceJobVLBO : serviceJobList) {
                serviceJobVLBO.setVatRate(defaultTaxRate);
            }
        }
        return new ValueListResultBO(serviceJobList, result.getTotal());
    }


    private BigDecimal getDefaultOrJobTaxRate(String taxRateType) {
        SystemParameterVO taxInfo = valueListService.findSysParamaterById(taxRateType);
        return taxInfo == null ? BigDecimal.TEN : new BigDecimal(taxInfo.getParameterValue());
    }

    public ValueListResultBO findServiceSpList(BaseVLForm model) {
        return valueListService.findServiceSpList(model);
    }

    public Page<LocationVLBO> findLocationList(LocationVLForm model,String siteID){

        Page<LocationVLBO> list = null;
        //库存减
        if (StringUtils.equals(model.getArg0(), CommonConstants.CHAR_Y)) {
            list = valueListService.findLocationListByProductInventoty(model, siteID);
        }

        //库存加
        if ((!StringUtils.equals(model.getArg0(), CommonConstants.CHAR_Y))){
            list = valueListService.findLocationListByLocation(model, siteID,model.getLocationTypeList());

            // if (!Nulls.isNull(model.getPartsId())){
            //     List<LocationVLBO> content = list.getContent();
            //     List<Long> locationIds = content.stream().map(LocationVLBO::getLocationId).collect(Collectors.toList());
            //     List<ProductInventoryVO> productInventoryList = valueListService.findMainLocationByProInv(model.getPointId(),model.getPartsId(),siteID,locationIds);
            //     List<Long> primaryInventoryIds = productInventoryList.stream().filter(vo -> CommonConstants.CHAR_Y.equals(vo.getPrimaryFlag())).map(ProductInventoryVO::getLocationId).collect(Collectors.toList());
            //     for (LocationVLBO locationVLBO : list.getContent()) {
            //         if(primaryInventoryIds.contains(locationVLBO.getLocationId())){
            //             locationVLBO.setMainLocation(CommonConstants.CHAR_Y);
            //         }else{
            //             locationVLBO.setMainLocation(CommonConstants.CHAR_N);
            //         }
            //     }
            // }
        }

        if(!ObjectUtils.isEmpty(list)){
            Map<String,String> map=new HashMap<>();
            map.put(PJConstants.LocationType.FROZEN.getCodeDbid(),PJConstants.LocationType.FROZEN.getCodeData1());
            map.put(PJConstants.LocationType.NORMAL.getCodeDbid(),PJConstants.LocationType.NORMAL.getCodeData1());
            map.put(PJConstants.LocationType.SERVICE.getCodeDbid(),PJConstants.LocationType.SERVICE.getCodeData1());
            map.put(PJConstants.LocationType.TENTATIVE.getCodeDbid(),PJConstants.LocationType.TENTATIVE.getCodeData1());

            for (LocationVLBO locationVLBO : list.getContent()) {
                locationVLBO.setLocationType((map.get(locationVLBO.getLocationTypeCd())));
            }
        }

        return list;
    }

    public ValueListResultBO findSymptomList(BaseVLForm model) {

        List<CmmSymptomVO> cmmSymptomVOs  =valueListService.findByProductSectionId(Long.parseLong(model.getArg0()));

        List<SymptomVLBO> list= new ArrayList<SymptomVLBO>();
        for (CmmSymptomVO cmmSymptomVO : cmmSymptomVOs) {

            SymptomVLBO bo =new SymptomVLBO();

            bo.setName(cmmSymptomVO.getDescription());
            bo.setCode(cmmSymptomVO.getSymptomCd());
            bo.setId(Long.toString(cmmSymptomVO.getSymptomId()));
            bo.setSectionId(Long.toString(cmmSymptomVO.getProductSectionId()));

            list.add(bo);
        }

        return new ValueListResultBO(list);
    }

    public ValueListResultBO findServicePackageList(BaseVLForm model, String siteId) {
        //arg0 = cmmSerializedProId , arg1 = serviceCategoryId
        return valueListService.findServicePackageList(model, siteId);
    }

    /**
     * @author Peng Zhengan
     */
    public ValueListResultBO findCustomerValueList(BaseVLForm model, String siteId) {

        return valueListService.findCustomerValueList(model, siteId);
    }

    private ValueListResultBO setValue2PartsBO(ValueListResultBO result, PartsVLForm model, String siteId, String taxRateFromDate, boolean yamahaFlag) {

		List<PartsVLBO> list = (List<PartsVLBO>) result.getList();

        BigDecimal defaultTaxRate = getDefaultOrJobTaxRate(MstCodeConstants.SystemParameterType.TAXRATE);
        for (PartsVLBO part: list) {
            part.setCodeFmt(PartNoUtil.format(part.getCode()));
            part.setTaxRate(defaultTaxRate);
            part.setOnHandQty(BigDecimal.ZERO);
            if (StringUtils.isNotBlank(part.getDesc()) && part.getDesc().contains(CommonConstants.CHAR_SPACE)) {
                String[] desc = part.getDesc().split(CommonConstants.CHAR_SPACE, CommonConstants.INTEGER_TWO);
                part.setDesc(PartNoUtil.format(desc[0]) + CommonConstants.CHAR_SPACE + desc[1]);
            }
        }
        Map<String, PartsVLBO> map = list.stream().collect(Collectors.toMap(PartsVLBO::getId, partsVLBO -> partsVLBO));
        if (!list.isEmpty() && StringUtils.isNotBlank(model.getAutoFillFlag()) && !Nulls.isNull(model.getFacilityId())) {
            if (StringUtils.equals(CommonConstants.CHAR_Y, model.getAutoFillFlag())) {
                List<Long> productIds = list.stream().map(PartsVLBO::getId).map(Long::valueOf).toList();
                List<PartsVLBO> supersedingPartsIdList = valueListService.findSupersedingPartsIdList(productIds);
                List<PartsVLBO> mainLocationIdList = valueListService.findMainLocationIdList(productIds, siteId, model.getFacilityId(), yamahaFlag);
                List<PartsVLBO> onHandQtyList = valueListService.findOnHandQtyList(productIds, siteId, model.getFacilityId(), yamahaFlag);
                List<PartsVLBO> productTaxList= valueListService.findProductTaxList(productIds);
                supersedingPartsIdList.forEach(part -> {
                    PartsVLBO partsVLBO = map.get(part.getId());
                        if (!Nulls.isNull(partsVLBO)) {
                            partsVLBO.setSupersedingPartsId(part.getSupersedingPartsId());
                            partsVLBO.setSupersedingPartsCd(part.getSupersedingPartsCd());
                            partsVLBO.setSupersedingPartsCdFmt(PartNoUtil.format(part.getSupersedingPartsCd()));
                            partsVLBO.setSupersedingPartsNm(part.getSupersedingPartsNm());
                        }
                });
                mainLocationIdList.forEach(part -> {
                    PartsVLBO partsVLBO = map.get(part.getId());
                        if (!Nulls.isNull(partsVLBO)) {
                            partsVLBO.setMainLocationId(part.getMainLocationId());
                            partsVLBO.setMainLocationCd(part.getMainLocationCd());
                        }
                });
                onHandQtyList.forEach(part -> {
                    PartsVLBO partsVLBO = map.get(part.getId());
                        if (!Nulls.isNull(partsVLBO)) {
                            partsVLBO.setOnHandQty(part.getOnHandQty());
                        }
                });
                productTaxList.forEach(part -> {
                    PartsVLBO partsVLBO = map.get(part.getId());
                    if (StringUtils.isNotBlank(taxRateFromDate) && DateUtils.isAfterOrEqualDate(DateUtils.getCurrentDate(), DateUtils.string2Date(taxRateFromDate, DateUtils.FORMAT_YMD_NODELIMITER)) && !Nulls.isNull(partsVLBO)) {
                        partsVLBO.setTaxRate(part.getTaxRate());
                    }
                });

                List<PartsVLBO> resultList = map.values().stream().toList();

                return new ValueListResultBO(resultList, result.getTotal());
            }
        }

		return result;
	}

	private void setPartsQueryModel(PartsVLForm model) {

		//下拉框查询
        if (StringUtils.isNotBlank(model.getContent())) {

            if (model.getContent().contains(CommonConstants.CHAR_SPACE)) {

                String[] content = model.getContent().split(CommonConstants.CHAR_SPACE, CommonConstants.INTEGER_TWO);
                model.setContent(PartNoUtil.formaForDB(content[0]) + CommonConstants.CHAR_SPACE + content[1]);

            } else {
                model.setContent(PartNoUtil.formaForDB(model.getContent()));
            }
        }

        //侧边栏查询
        if (StringUtils.isNotBlank(model.getCode())) {

            if (model.getCode().contains(CommonConstants.CHAR_SPACE)) {

                String[] code = model.getCode().split(CommonConstants.CHAR_SPACE, CommonConstants.INTEGER_TWO);
                model.setCode(PartNoUtil.formaForDB(code[0]) + CommonConstants.CHAR_SPACE + code[1]);

            } else {
                model.setCode(PartNoUtil.formaForDB(model.getCode()));
            }
        }
        // 配件编码：不要模糊匹配
        if (StringUtils.isNotBlank(model.getPartsCd())) {
        	model.setPartsCd(PartNoUtil.formaForDB(model.getPartsCd()));
        }
	}

	public ValueListResultBO findPromotionValueList(BaseVLForm model) {

        return valueListService.findPromotionValueList(model);
    }
}
