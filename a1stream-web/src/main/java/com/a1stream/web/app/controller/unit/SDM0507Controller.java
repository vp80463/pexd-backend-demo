package com.a1stream.web.app.controller.unit;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM050701BO;
import com.a1stream.domain.form.unit.SDM050701Form;
import com.a1stream.unit.facade.SDM0507Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Waiting Screen
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdm0507")
@FunctionId("SDM0507")
public class SDM0507Controller implements RestProcessAware {

    @Resource
    private SDM0507Facade sdm0507Facade;

    @PostMapping(value = "/getWaitingScreenList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SDM050701BO> getWaitingScreenList(@RequestBody final SDM050701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setUserId(uc.getUserId());
        return sdm0507Facade.getWaitingScreenList(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SDM050701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        sdm0507Facade.confirm(form);
    }

}
