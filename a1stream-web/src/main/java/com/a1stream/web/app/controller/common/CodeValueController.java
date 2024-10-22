package com.a1stream.web.app.controller.common;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.CodeValueFacade;
import com.a1stream.common.model.PrivacyPolicyCVBO;
import com.a1stream.common.model.PrivacyPolicyCVForm;
import com.a1stream.common.model.YmvnStockBO;
import com.a1stream.common.model.YmvnStockForm;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("common/codevalue")
public class CodeValueController implements RestProcessAware{

    @Resource
    private CodeValueFacade codeValueFacade;

    @PostMapping(value = "/getPrivacyPolicyResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public PrivacyPolicyCVBO getPrivacyPolicyResult(@RequestBody final PrivacyPolicyCVForm model, @AuthenticationPrincipal final PJUserDetails uc) {

        return codeValueFacade.getPrivacyPolicyResult(model, uc.getDealerCode());
    }

    @PostMapping(value = "/getYmvnStock.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public YmvnStockBO getYmvnStock(@RequestBody final YmvnStockForm form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return codeValueFacade.getYmvnStock(form);

    }
}