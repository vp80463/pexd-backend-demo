package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM070503BO;
import com.a1stream.domain.bo.master.CMM070504BO;
import com.a1stream.domain.form.master.CMM070503Form;
import com.a1stream.domain.form.master.CMM070504Form;
import com.a1stream.master.facade.CMM0705Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0705")
public class CMM0705Controller implements RestProcessAware{

    @Resource
    private CMM0705Facade cmm0705Facade;

    @PostMapping(value = "/findRoleList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM070503BO> findRoleList(@RequestBody final CMM070503Form model
                                        , @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0705Facade.findRoleList(uc.getDealerCode()
                                        , model);
    }

    @PostMapping(value = "/getRoleAuthInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM070504BO getRoleAuthInfo(@RequestBody final CMM070504Form model
                                     , @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0705Facade.getRoleAuthInfo(uc.getDealerCode(), model);
    }
}