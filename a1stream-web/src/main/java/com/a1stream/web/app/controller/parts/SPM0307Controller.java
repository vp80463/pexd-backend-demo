/**
 *
 */
package com.a1stream.web.app.controller.parts;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030701BO;
import com.a1stream.domain.form.parts.SPM030701Form;
import com.a1stream.parts.facade.SPM0307Facade;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spm0307")
@FunctionId("SPM0307")
public class SPM0307Controller {

    @Resource
    private SPM0307Facade spm0307Facade;

    @PostMapping(value = "/getLocationQty.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM030701BO getLocationQty(@RequestBody final SPM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0307Facade.getLocationQty(form);
    }

    @PostMapping(value = "/getLocationInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM030701BO getLocationInfo(@RequestBody final SPM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0307Facade.getLocationInfo(form);
    }

    @PostMapping(value = "/partsStockAdjustmentConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void partsStockAdjustmentConfirm(@RequestBody final SPM030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0307Facade.partsStockAdjustmentConfirm(form);
    }
}
