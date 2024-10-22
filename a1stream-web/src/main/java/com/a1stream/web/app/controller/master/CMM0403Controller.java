package com.a1stream.web.app.controller.master;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM040301BO;
import com.a1stream.domain.form.master.CMM040301Form;
import com.a1stream.master.facade.CMM0403Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Model Info Inquiry明细画面
*
* mid2330
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Liu Chaoran     New
*/
@RestController
@RequestMapping("master/cmm0403")
@FunctionId("CMM0403")
public class CMM0403Controller implements RestProcessAware{

    @Resource
    private CMM0403Facade cmm0403Facade;

    @PostMapping(value = "/getModelInfoInquiry.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<CMM040301BO> getModelInfoInquiry(@RequestBody final CMM040301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0403Facade.getModelInfoInquiry(form, uc.getDealerCode());
    }

}