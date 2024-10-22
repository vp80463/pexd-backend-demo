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
import com.a1stream.domain.bo.parts.SPM020201BO;
import com.a1stream.domain.form.parts.SPM020201Form;
import com.a1stream.parts.facade.SPM0202Facade;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spm0202")
@FunctionId("SPM0202")
public class SPM0202Controller {
    @Resource
    private SPM0202Facade spm0202Facade;

    @PostMapping(value = "/searchInvoiceInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020201BO searchInvoiceInfo(@RequestBody final SPM020201Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spm0202Facade.searchInvoiceInfo(form);
    }

    @PostMapping(value = "/confirmSalesReturn.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020201Form confirmSalesReturn(@RequestBody final SPM020201Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
       return spm0202Facade.confirmSalesReturn(form);
    }

}
