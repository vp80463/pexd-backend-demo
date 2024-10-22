package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM030201BO;
import com.a1stream.domain.form.unit.SDM030201Form;
import com.a1stream.unit.facade.SDM0302Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Retail Order Entry
*
* mid2303
* 2024年9月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/29   Ruan Hansheng   New 
*/
@RestController
@RequestMapping("unit/sdm0302")
@FunctionId("SDM0302")
public class SDM0302Controller implements RestProcessAware {

    @Resource
    private SDM0302Facade sdm0302Facade;

    @PostMapping(value = "/getInitResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030201BO getInitResult(@RequestBody final SDM030201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030201BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        return sdm0302Facade.getInitResult(form);
    }

    @PostMapping(value = "/getMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030201BO getMotorcycleInfo(@RequestBody final SDM030201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0302Facade.getMotorcycleInfo(form);
    }

    @PostMapping(value = "/delivery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030201BO delivery(@RequestBody final SDM030201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030201BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0302Facade.check(form);
        return sdm0302Facade.delivery(form);
    }
}
