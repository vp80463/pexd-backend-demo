package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM020201BO;
import com.a1stream.domain.bo.master.CMM020202BO;
import com.a1stream.domain.form.master.CMM020202Form;
import com.a1stream.master.facade.CMM0202Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0202")
public class CMM0202Controller implements RestProcessAware{

    @Resource
    private CMM0202Facade cmm0202Facade;

    @PostMapping(value = "/getEmployeeInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM020201BO> getEmployeeInfoList(@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0202Facade.getEmployeeInfoListBySiteId(uc.getDealerCode());
    }

    @PostMapping(value = "/getEmployeeDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM020202BO getUserDetail(@RequestBody final CMM020202Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0202Facade.getEmployeeDetail(model.getPersonId(), uc.getDealerCode());
    }

    @PostMapping(value = "/newModifyEmployee.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdateEmployee(@RequestBody final CMM020202Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        cmm0202Facade.saveOrUpdateEmployee(model, uc.getDealerCode());
    }
}