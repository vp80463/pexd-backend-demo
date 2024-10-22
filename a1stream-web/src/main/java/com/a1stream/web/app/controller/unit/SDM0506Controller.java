package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM050601BO;
import com.a1stream.domain.form.unit.SDM050601Form;
import com.a1stream.unit.facade.SDM0506Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Update Period Maintenance
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/28   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdm0506")
@FunctionId("SDM0506")
public class SDM0506Controller implements RestProcessAware {

    @Resource
    private SDM0506Facade sdm0506Facade;

    @PostMapping(value = "/getUpdPeriodMaintList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM050601BO> getUpdPeriodMaintList(@RequestBody final SDM050601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setDefaultPointId(uc.getDefaultPointId());
        form.setUserId(uc.getUserId());
        return sdm0506Facade.getUpdPeriodMaintList(form);
    }
    
    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SDM050601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setDefaultPointId(uc.getDefaultPointId());
        sdm0506Facade.confirm(form);
    }

}
