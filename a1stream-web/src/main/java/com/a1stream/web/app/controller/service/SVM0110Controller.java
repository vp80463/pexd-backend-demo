package com.a1stream.web.app.controller.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.service.SVM011001BO;
import com.a1stream.domain.form.service.SVM011001Form;
import com.a1stream.service.facade.SVM0110Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("service/svm0110")
@FunctionId("SVM0110")
public class SVM0110Controller implements RestProcessAware{

    @Resource
    private SVM0110Facade svm0110Facade;

    @PostMapping(value = "/getMotorcyclyInfoByFrameNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM011001Form getMotorcyclyInfoByFrameNo(@RequestBody final SVM011001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0110Facade.getMotorcyclyInfoByFrameNo(form);
    }

    @PostMapping(value = "/getItemInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM011001BO> getItemInfoList(@RequestBody final SVM011001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0110Facade.getItemInfoList(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SVM011001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        svm0110Facade.confirm(form);
    }
}