package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM030701BO;
import com.a1stream.domain.form.unit.SDM030701Form;
import com.a1stream.unit.facade.SDM0307Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:  Retail Order Entry For DO
*
* mid2303
* 2024年10月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/14   Ruan Hansheng   New 
*/
@RestController
@RequestMapping("unit/sdm0307")
@FunctionId("SDM0307")
public class SDM0307Controller implements RestProcessAware {

    @Resource
    private SDM0307Facade sdm0307Facade;

    @PostMapping(value = "/getInitResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030701BO getInitResult(@RequestBody final SDM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030701BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        return sdm0307Facade.getInitResult(form);
    }

    @PostMapping(value = "/getMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030701BO getMotorcycleInfo(@RequestBody final SDM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0307Facade.getMotorcycleInfo(form);
    }

    @PostMapping(value = "/save.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030701BO save(@RequestBody final SDM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030701BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0307Facade.check(form);
        return sdm0307Facade.save(form);
    }

    @PostMapping(value = "/delivery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030701BO delivery(@RequestBody final SDM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030701BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0307Facade.check(form);
        return sdm0307Facade.delivery(form);
    }
}
