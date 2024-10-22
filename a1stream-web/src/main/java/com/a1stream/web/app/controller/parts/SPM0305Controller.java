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
import com.a1stream.domain.bo.parts.SPM030501BO;
import com.a1stream.domain.form.parts.SPM030501Form;
import com.a1stream.parts.facade.SPM0305Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Stock Movement
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@RestController
@RequestMapping("parts/spm0305")
@FunctionId("SPM0305")
public class SPM0305Controller implements RestProcessAware {

    @Resource
    private SPM0305Facade spm0305Facade;

    @PostMapping(value = "/getPartsLocationList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SPM030501BO> getPartsLocationList(@RequestBody final SPM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0305Facade.getPartsLocationList(form);
    }

    @PostMapping(value = "/confirmPartsLocationStockMovement.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPartsLocationStockMovement(@RequestBody final SPM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        spm0305Facade.confirmPartsLocationStockMovement(form);
    }
}
