package com.a1stream.web.app.controller.master;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM020401BO;
import com.a1stream.domain.form.master.CMM020401Form;
import com.a1stream.master.facade.CMM0204Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0204")
public class CMM0204Controller implements RestProcessAware{

    @Resource
    private CMM0204Facade cmm0204Facade;

    @PostMapping(value = "/getCompanyInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM020401BO getCompanyInfo(@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0204Facade.getCompanyInfo(uc.getDealerCode());
    }

    @PostMapping(value = "/updateCompanyInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateCompanyInfo(@RequestBody final CMM020401Form model
                                , @AuthenticationPrincipal final PJUserDetails uc) {

        cmm0204Facade.updateCompanyInfo(uc.getDealerCode()
                                      , model);
    }
}