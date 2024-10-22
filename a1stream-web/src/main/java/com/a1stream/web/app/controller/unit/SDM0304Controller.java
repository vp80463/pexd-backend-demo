package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM030401BO;
import com.a1stream.domain.form.unit.SDM030401Form;
import com.a1stream.unit.facade.SDM0304Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Special Order Entry
*
* mid2303
* 2024年10月8日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/10/08   Ruan Hansheng   New 
*/
@RestController
@RequestMapping("unit/sdm0304")
@FunctionId("SDM0304")
public class SDM0304Controller implements RestProcessAware {

    @Resource
    private SDM0304Facade sdm0304Facade;

    @PostMapping(value = "/getInitResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030401BO getInitResult(@RequestBody final SDM030401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030401BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        return sdm0304Facade.getInitResult(form);
    }

    @PostMapping(value = "/getMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030401BO getMotorcycleInfo(@RequestBody final SDM030401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0304Facade.getMotorcycleInfo(form);
    }

    @PostMapping(value = "/save.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030401BO save(@RequestBody final SDM030401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030401BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0304Facade.check(form);
        return sdm0304Facade.save(form);
    }

    @PostMapping(value = "/delivery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030401BO delivery(@RequestBody final SDM030401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        SDM030401BO formData = form.getFormData();
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        formData.setFacilityId(uc.getDefaultPointId());
        formData.setFacilityCd(uc.getDefaultPointCd());
        formData.setFacilityNm(uc.getDefaultPointNm());
        sdm0304Facade.check(form);
        return sdm0304Facade.delivery(form);
    }
}
