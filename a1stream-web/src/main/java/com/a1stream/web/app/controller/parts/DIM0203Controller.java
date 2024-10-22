package com.a1stream.web.app.controller.parts;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.form.parts.DIM020301Form;
import com.a1stream.parts.facade.DIM0203Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:re-upload Parts Data
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/dim0203")
@FunctionId("DIM0203")
public class DIM0203Controller implements RestProcessAware {

    @Resource
    private DIM0203Facade dim0203Facade;

    @PostMapping(value = "/doReUploadComplete.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doReUploadComplete(@RequestBody final DIM020301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0203Facade.doReUploadComplete(form.getPointId(), uc.getDealerCode());
    }
}
