package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM031501BO;
import com.a1stream.domain.form.parts.SPM031501Form;
import com.a1stream.parts.facade.SPM0315Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* mid2287
* 2024年6月13日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/13   Wang Nan      New
*/

@RestController
@RequestMapping("parts/spm0315")
@FunctionId("SPM0315")
public class SPM0315Controller implements RestProcessAware {

    @Resource
    private SPM0315Facade spm0315Facade;

    @PostMapping(value = "/getPartsStockRegisterList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM031501BO> getPartsStockRegisterList(@RequestBody final SPM031501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0315Facade.getPartsStockRegisterList(form);
    }

    @PostMapping(value = "/confirmPartsStockRegister.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPartsStockRegister(@RequestBody final SPM031501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        spm0315Facade.confirmPartsStockRegister(form, uc);
    }
}
