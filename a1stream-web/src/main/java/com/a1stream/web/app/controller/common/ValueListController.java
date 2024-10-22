package com.a1stream.web.app.controller.common;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.ValueListFacade;
import com.a1stream.common.model.BaseVLForm;
import com.a1stream.common.model.ConsumerVLForm;
import com.a1stream.common.model.DemandVLForm;
import com.a1stream.common.model.LocationVLBO;
import com.a1stream.common.model.LocationVLForm;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ServiceJobVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("common/valuelist")
public class ValueListController implements RestProcessAware{

    @Resource
    private ValueListFacade valueListFacade;

    @PostMapping(value = "/userRoleList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findUserRoleList(@AuthenticationPrincipal final PJUserDetails uc, @RequestBody final BaseVLForm model) {

        return valueListFacade.findUserRoleList(uc, model);
    }

    @PostMapping(value = "/employeeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findEmployeeList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return valueListFacade.findEmployeeList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/pointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO pagePointWithCache(@AuthenticationPrincipal final PJUserDetails uc, @RequestBody final BaseVLForm model) {

        return valueListFacade.findPointList(uc, model);
    }

    @PostMapping(value = "/supplierList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findSupplierList(@AuthenticationPrincipal final PJUserDetails uc, @RequestBody final BaseVLForm model) {

        return valueListFacade.findSupplierList(uc.getDealerCode(), model);
    }

    @PostMapping(value = "/dealerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO pageDealerWithCache(@RequestBody final BaseVLForm model) {
        return valueListFacade.findDealerList(model);
    }

    @PostMapping(value = "/userList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findUserValueList(@RequestBody final BaseVLForm model) {

        return valueListFacade.findUserValueList(model);
    }

    @PostMapping(value = "/modelList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findModelList(@RequestBody final BaseVLForm model) {

        return valueListFacade.findModelList(model);
    }

    @PostMapping(value = "/sdModelList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findSdModelList(@RequestBody final BaseVLForm model) {

        return valueListFacade.findSdModelList(model);
    }

    @PostMapping(value = "/partsList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findPartsList(@RequestBody final PartsVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return valueListFacade.findPartsList(model, uc.getDealerCode(), uc.getTaxPeriod());
    }

    @PostMapping(value = "/yamahaPartsList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findYamahaPartsList(@RequestBody final PartsVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        return valueListFacade.findYamahaPartsList(model, uc.getTaxPeriod());
    }

    @PostMapping(value = "/sectionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findSectionList(@RequestBody final BaseVLForm model) {

        return valueListFacade.findSectionList(model);
    }

    @PostMapping(value = "/packageList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findPackageList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return valueListFacade.findPackageList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/demandList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO pageServiceDemandWithPeriodWithCache(@RequestBody final DemandVLForm model) {

        return valueListFacade.findDemandList(model);

    }

    @PostMapping(value = "/consumerByUnitList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findConsumerByUnitList(@RequestBody final ConsumerVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return valueListFacade.findConsumerByUnitList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/faultCodeValueList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findFaultCodeList(@RequestBody final BaseVLForm model) {
        return valueListFacade.findFaultCodeList(model);

    }

    @PostMapping(value = "/faultDescriptionValueList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findFaultDescriptionList(@RequestBody final BaseVLForm model) {
        return valueListFacade.findFaultDescriptionList(model);

    }

    @PostMapping(value = "/serviceJobList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findServiceJobList(@RequestBody final BaseVLForm model) {
        return valueListFacade.findServiceJobList(model);
    }

    @PostMapping(value = "/serviceJobByModelList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findServiceJobByModelList(@RequestBody final ServiceJobVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return valueListFacade.findServiceJobByModelList(model, uc.getTaxPeriod());
    }

    @PostMapping(value = "/serviceJobByModelTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findServiceJobByModelTypeList(@RequestBody final ServiceJobVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return valueListFacade.findServiceJobByModelTypeList(model, uc.getTaxPeriod());
    }

    @PostMapping(value = "/serviceSpList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findServiceSpList(@RequestBody final BaseVLForm model) {
        return valueListFacade.findServiceSpList(model);
    }

    @PostMapping(value = "/locationValueList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<LocationVLBO> findLocationList(@RequestBody final LocationVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return valueListFacade.findLocationList(model,uc.getDealerCode());
    }

    @PostMapping(value = "/symptomValueListWithCache.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO symptomValueListWithCache(@RequestBody final BaseVLForm model) {
        return valueListFacade.findSymptomList(model);
    }

    @PostMapping(value = "/customerValueList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO customerValueList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return valueListFacade.findCustomerValueList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/promotionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO pagePromotionWithCache(@RequestBody final BaseVLForm model) {
        return valueListFacade.findPromotionValueList(model);
    }

    @PostMapping(value = "/servicePackageList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValueListResultBO findServicePackageList(@RequestBody final BaseVLForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return valueListFacade.findServicePackageList(model, uc.getDealerCode());
    }
}