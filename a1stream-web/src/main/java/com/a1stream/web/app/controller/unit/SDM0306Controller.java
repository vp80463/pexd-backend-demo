package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM030601BO;
import com.a1stream.domain.form.unit.SDM030601Form;
import com.a1stream.unit.facade.SDM0306Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Employee Order Entry 
*
* mid2303
* 2024年10月11日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/11   Ruan Hansheng   New 
*/
@RestController
@RequestMapping("unit/sdm0306")
@FunctionId("SDM0306")
public class SDM0306Controller implements RestProcessAware {

    @Resource
    private SDM0306Facade sdm0306Facade;

    @PostMapping(value = "/getInitResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030601BO getInitResult(@RequestBody final SDM030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030601BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        return sdm0306Facade.getInitResult(form);
    }

    @PostMapping(value = "/getMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030601BO getMotorcycleInfo(@RequestBody final SDM030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0306Facade.getMotorcycleInfo(form);
    }

    @PostMapping(value = "/save.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030601BO save(@RequestBody final SDM030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030601BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0306Facade.check(form);
        return sdm0306Facade.save(form);
    }

    @PostMapping(value = "/delivery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030601BO delivery(@RequestBody final SDM030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030601BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0306Facade.check(form);
        return sdm0306Facade.delivery(form);
    }
}
