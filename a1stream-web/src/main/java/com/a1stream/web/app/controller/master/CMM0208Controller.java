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
import com.a1stream.domain.bo.master.CMM020801BO;
import com.a1stream.domain.form.master.CMM020801Form;
import com.a1stream.master.facade.CMM0208Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Trader Info List明细画面
*
* mid2330
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Liu Chaoran     New
*/
@RestController
@RequestMapping("master/cmm0208")
@FunctionId("CMM0208")
public class CMM0208Controller implements RestProcessAware {

    @Resource
    private CMM0208Facade cmm0208Facade;

    @PostMapping(value = "/findTraderInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM020801BO> findTraderInfoList(@RequestBody final CMM020801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0208Facade.findTraderInfoList(form, uc.getDealerCode());
    }

}