package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMQ070101BO;
import com.a1stream.domain.bo.master.CMQ070102BO;
import com.a1stream.domain.form.master.CMQ070101Form;
import com.a1stream.domain.form.master.CMQ070102Form;
import com.a1stream.master.facade.CMQ0701Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmq0701")
public class CMQ0701Controller implements RestProcessAware{

    @Resource
    private CMQ0701Facade cmq0701Facade;

    @PostMapping(value = "/findUserList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMQ070101BO> findUserList(@RequestBody final CMQ070101Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmq0701Facade.findUserList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/getUserDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMQ070102BO getUserDetail(@RequestBody final CMQ070102Form model) {

        return cmq0701Facade.getUserDetail(model.getUserId());
    }

    @PostMapping(value = "/newOrModifyUser.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdateUser(@RequestBody final CMQ070102Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        cmq0701Facade.saveOrUpdateUser(model, uc.getDealerCode());
    }

    @PostMapping(value = "/resetPassword.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void resetUserPassword(@RequestBody final CMQ070102Form model, @AuthenticationPrincipal final PJUserDetails uc) {
        cmq0701Facade.resetUserPassword(model, uc.getDealerCode());
    }

    @PostMapping(value = "/deleteUser.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteUser(@RequestBody final CMQ070101Form model) {

        cmq0701Facade.deleteUser(model);
    }
}