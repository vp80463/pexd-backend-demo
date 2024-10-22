package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM040103BO;
import com.a1stream.domain.form.unit.SDM040103Form;
import com.a1stream.unit.facade.SDM0401Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@RestController
@RequestMapping("unit/sdm0401")
public class SDM0401Controller implements RestProcessAware{

    @Resource
    private SDM0401Facade sdm0401Fac;
    /**
     * 查询
     */
    @PostMapping(value = "/findWarrantyCard.json")
    public SDM040103BO findWarrantyCard(@RequestBody final SDM040103Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0401Fac.findWarrantyCard(form);
    }

    /**
     * 修改Warranty Card
     */
    @PostMapping(value = "/updateWarrantyCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateWarrantyCard(@RequestBody final SDM040103Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        sdm0401Fac.updateWarrantyCard(model);
    }
}