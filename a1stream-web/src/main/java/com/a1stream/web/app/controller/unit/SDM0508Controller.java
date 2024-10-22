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
import com.a1stream.domain.bo.unit.SDM050801BO;
import com.a1stream.domain.form.unit.SDM050801Form;
import com.a1stream.unit.facade.SDM0508Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Promotion Data Recovery
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdm0508")
@FunctionId("SDM0508")
public class SDM0508Controller implements RestProcessAware {

    @Resource
    private SDM0508Facade sdq0508Facade;

    @PostMapping(value = "/getPromoRecList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM050801BO> getPromoRecList(@RequestBody final SDM050801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdq0508Facade.getPromoRecList(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SDM050801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        sdq0508Facade.confirm(form);
    }

}
