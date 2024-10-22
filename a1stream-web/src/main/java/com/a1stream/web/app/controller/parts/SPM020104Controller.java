package com.a1stream.web.app.controller.parts;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.form.parts.SPM020104Form;
import com.a1stream.parts.facade.SPM020104Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年07月04日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/04   Ruan Hansheng     New
*/
@RestController
@RequestMapping("parts/spm020104")
@FunctionId("SPM020104")
public class SPM020104Controller implements RestProcessAware {

    @Resource
    private SPM020104Facade spm020104Facade;

    @PostMapping(value = "/getPickingItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020104Form getPickingItemList(@RequestBody final SPM020104Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm020104Facade.getPickingItemList(form, uc);
    }

    @PostMapping(value = "/shipment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void shipment(@RequestBody final SPM020104Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm020104Facade.shipment(form, uc);
    }

}
