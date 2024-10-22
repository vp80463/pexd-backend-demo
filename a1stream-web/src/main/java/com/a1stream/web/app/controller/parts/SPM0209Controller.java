package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM020901BO;
import com.a1stream.domain.form.parts.SPM020901Form;
import com.a1stream.parts.facade.SPM0209Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Picking Discrepancy Entry
*
* mid2330
* 2024年7月4日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/04   Liu Chaoran   New
*/
@RestController
@RequestMapping("parts/spm0209")
public class SPM0209Controller implements RestProcessAware {

    @Resource
    private SPM0209Facade spm0209Facade;

    @PostMapping(value = "/findPickingDiscEntryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM020901BO> findPickingDiscEntryList(@RequestBody final SPM020901Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0209Facade.findPickingDiscEntryList(form);
    }

    @PostMapping(value = "/comfirmPickingDiscEntryData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020901Form comfirmPickingDiscEntryData(@RequestBody final SPM020901Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0209Facade.comfirmPickingDiscEntryData(form);
    }
}
