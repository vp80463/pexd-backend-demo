package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM040101BO;
import com.a1stream.domain.form.parts.SPM040101Form;
import com.a1stream.parts.facade.SPM0401Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spm0401")
public class SPM0401Controller implements RestProcessAware{

    @Resource
    private SPM0401Facade spm0401Facade;

    @PostMapping(value = "/searchRoqRopDefineList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM040101BO> searchRoqRopDefineList(@RequestBody final SPM040101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return spm0401Facade.searchRoqRopDefineList(form, uc.getDealerCode());
    }
}