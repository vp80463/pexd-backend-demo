/**
 *
 */
package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM021101BO;
import com.a1stream.domain.form.parts.SPM021101Form;
import com.a1stream.parts.facade.SPM0211Facade;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2215
*/
@RestController
@RequestMapping("parts/spm0211")
@FunctionId("SPM0211")
public class SPM0211Controller {

    @Resource
    private SPM0211Facade spm0211Facade;

    @PostMapping(value = "/findShipmentComplationList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM021101BO> findShipmentCompletionList(@RequestBody final SPM021101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spm0211Facade.findShipmentCompletionList(form);
    }

    @PostMapping(value = "/confirmShipment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmShipment(@RequestBody final SPM021101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0211Facade.confirmShipment(form);
    }
}
