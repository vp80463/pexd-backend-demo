package com.a1stream.web.app.controller.master;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMQ050101BO;
import com.a1stream.domain.form.master.CMQ050101Form;
import com.a1stream.master.facade.CMQ0501Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@RestController
@RequestMapping("master/cmq0501")
public class CMQ0501Controller implements RestProcessAware{

    @Resource
    private CMQ0501Facade cmq0501Facade;

    @PostMapping(value = "/findInformationReportList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMQ050101BO findPartsInformationReportList(@RequestBody final CMQ050101Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmq0501Facade.findPartsInformationReportList(model, uc.getDealerCode());
    }

}