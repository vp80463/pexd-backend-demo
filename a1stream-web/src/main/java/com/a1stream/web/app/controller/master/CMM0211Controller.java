package com.a1stream.web.app.controller.master;


import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM021101BO;
import com.a1stream.domain.form.master.CMM021101Form;
import com.a1stream.master.facade.CMM0211Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0211")
public class CMM0211Controller implements RestProcessAware{

    @Resource
    private CMM0211Facade cmm0211Facade;

    @PostMapping(value = "/findBinTypeList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM021101BO> findBinTypeList(@AuthenticationPrincipal final PJUserDetails uc) {
        return cmm0211Facade.findBinTypeList(uc.getDealerCode());
    }

    @PostMapping(value = "/updateRow.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateRow(@RequestBody final CMM021101Form model,@AuthenticationPrincipal final PJUserDetails uc) {
        model.setSiteId(uc.getDealerCode());
        cmm0211Facade.updateRow(model);
    }

    @PostMapping(value = "/deleteRow.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteRow(@RequestBody final CMM021101Form model,@AuthenticationPrincipal final PJUserDetails uc) {
        model.setSiteId(uc.getDealerCode());
        cmm0211Facade.deleteRow(model);
    }

    @PostMapping(value = "/addRow.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addRow(@RequestBody final CMM021101Form model,@AuthenticationPrincipal final PJUserDetails uc) {
        model.setSiteId(uc.getDealerCode());
        cmm0211Facade.addRow(model);
    }
}