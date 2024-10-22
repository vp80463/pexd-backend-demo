package com.a1stream.web.app.controller.master;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM050801BO;
import com.a1stream.domain.form.master.CMM050801Form;
import com.a1stream.master.facade.CMM0508Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0508")
public class CMM0508Controller implements RestProcessAware{

    @Resource
    private CMM0508Facade cmm0508Facade;

    @PostMapping(value = "/findSeasonIndexList.json",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM050801BO> findSeasonIndexList(@RequestBody final CMM050801Form model,@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0508Facade.findSeasonIndexList(model,uc.getDealerCode());
    }

    @PostMapping(value = "/updateRow.json")
    public void updateRow(@RequestBody final CMM050801Form model,@AuthenticationPrincipal final PJUserDetails uc) {

         cmm0508Facade.updateRow(model,uc.getDealerCode());
    }

    @PostMapping(value = "/deleteRow.json")
    public void deleteRow(@RequestBody final CMM050801Form model,@AuthenticationPrincipal final PJUserDetails uc) {

         cmm0508Facade.deleteRow(model,uc.getDealerCode());
    }

    @PostMapping(value = "/addRow.json")
    public void addRow(@RequestBody final CMM050801Form model,@AuthenticationPrincipal final PJUserDetails uc) {

         cmm0508Facade.addRow(model,uc.getDealerCode());
    }
}