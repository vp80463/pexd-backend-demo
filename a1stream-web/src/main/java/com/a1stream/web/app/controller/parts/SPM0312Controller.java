package com.a1stream.web.app.controller.parts;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM031201BO;
import com.a1stream.domain.form.parts.SPM031201Form;
import com.a1stream.parts.facade.SPM0312Facade;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spm0312")
@FunctionId("SPM0312")
public class SPM0312Controller {

    @Resource
    private SPM0312Facade spm0312Facade;

    @PostMapping(value = "/getLocationQty.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM031201BO getLocationQty(@RequestBody final SPM031201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0312Facade.getLocationQty(form, uc.getDealerCode());
    }

    @PostMapping(value = "/partsForzenStockReleaseConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void partsForzenStockReleaseConfirm(@RequestBody final SPM031201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0312Facade.partsForzenStockReleaseConfirm(form, uc);
    }
}
