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
import com.a1stream.domain.bo.master.CMM070401BO;
import com.a1stream.domain.form.master.CMM070401Form;
import com.a1stream.master.facade.CMM0704Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: 菜单检查
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   sucao          New
*/
@RestController
@RequestMapping("master/cmm0704")
@FunctionId("CMM0704")
public class CMM0704Controller implements RestProcessAware {

    public static final String UPDATE_PROGRAM = "cmm0704";

    @Resource
    private CMM0704Facade cmm0704Fac;

    @PostMapping(value = "/getMenuCheckList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM070401BO> getMenuCheckList(@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0704Fac.getMenuCheckList(uc.getDealerCode());
    }

    @PostMapping(value = "/saveMenuCheckList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveMenuCheckList(@RequestBody final CMM070401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        cmm0704Fac.saveMenuCheckList(form.getData(), uc.getDealerCode());
    }
}