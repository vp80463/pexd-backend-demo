package com.a1stream.web.app.controller.master;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.master.CMM020501BO;
import com.a1stream.domain.bo.master.CMM020502BO;
import com.a1stream.domain.form.master.CMM020502Form;
import com.a1stream.master.facade.CMM0205Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("master/cmm0205")
public class CMM0205Controller implements RestProcessAware{

    @Resource
    private CMM0205Facade cmm0502Facade;

    @PostMapping(value = "/findPointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM020501BO> findPointList(@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0502Facade.findPointList(uc.getDealerCode());
    }

    @PostMapping(value = "/getPointDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM020502BO getPointDetail(@RequestBody final CMM020502Form model
                                    , @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0502Facade.getPointDetail(uc.getDealerCode()
                                          , model);
    }

    @PostMapping(value = "/updatePoint.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updatePoint(@RequestBody final CMM020502Form model
                          , @AuthenticationPrincipal final PJUserDetails uc) {

        cmm0502Facade.updatePoint(uc.getDealerCode()
                                , model);
    }
}