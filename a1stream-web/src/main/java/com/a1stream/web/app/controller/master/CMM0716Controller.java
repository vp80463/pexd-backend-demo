package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM071601BO;
import com.a1stream.domain.form.master.CMM071601Form;
import com.a1stream.master.facade.CMM0716Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: E-invoice Interface Result Check
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/06   Wang Nan      New
*/
@RestController
@RequestMapping("master/cmm0716")
@FunctionId("CMM0716")
public class CMM0716Controller implements RestProcessAware {

    @Resource
    private CMM0716Facade cmm0716Facade;

    @PostMapping(value = "/getInvoiceCheckResultList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM071601BO> getInvoiceCheckResultList(@RequestBody final CMM071601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0716Facade.getInvoiceCheckResultList(form);
    }

    @PostMapping(value = "/doResend.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doResend(@RequestBody final CMM071601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        cmm0716Facade.doResend(form);
    }

}