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
import com.a1stream.domain.bo.service.SVM020201BO;
import com.a1stream.domain.form.service.SVM020201Form;
import com.a1stream.domain.form.service.SVM020202Form;
import com.a1stream.service.facade.SVM0202Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("service/svm0202")
@FunctionId("SVM0202")
public class SVM0202Controller implements RestProcessAware{

    @Resource
    private SVM0202Facade svm0202Facade;

    @PostMapping(value = "/findServicePaymentList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM020201BO> findServicePaymentList(@RequestBody final SVM020201Form form, @AuthenticationPrincipal final PJUserDetails uc ) {

        return svm0202Facade.findServicePaymentList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/getServicePaymentDetaiList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM020201BO getServicePaymentDetaiList(@RequestBody final SVM020202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0202Facade.getServicePaymentDetaiList(form);
    }

    @PostMapping(value = "/confirmServicePaymentList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmServicePaymentList(@RequestBody final SVM020202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm0202Facade.confirmServicePaymentList(form, uc);
    }

    @PostMapping(value = "/issueServicePaymentList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void issueServicePaymentList(@RequestBody final SVM020202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm0202Facade.issueServicePaymentList(form, uc);
    }

    @PostMapping(value = "/print.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView print(@RequestBody final SVM020202Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return svm0202Facade.print(form);
    }

}