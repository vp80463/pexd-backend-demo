package com.a1stream.web.app.controller.parts;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.form.parts.SPM041301Form;
import com.a1stream.parts.facade.SPM0413Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@RestController
@RequestMapping("parts/spm0413")
@FunctionId("SPM0413")
public class SPM0413Controller implements RestProcessAware{

    @Resource
    private SPM0413Facade spm0413Facade;

    @PostMapping(value = "/confirmPurchaseOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPurchaseOrder(@RequestBody final SPM041301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0413Facade.confirmPurchaseOrder(form, uc);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM041301Form checkFile(@RequestBody final SPM041301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0413Facade.checkFile(form, uc);
    }
}
