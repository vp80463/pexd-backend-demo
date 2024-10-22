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
import com.a1stream.domain.bo.service.SVM020101BO;
import com.a1stream.domain.bo.service.SVM020102FreeCouponBO;
import com.a1stream.domain.form.service.SVM020101Form;
import com.a1stream.domain.form.service.SVM020102Form;
import com.a1stream.service.facade.SVM0201Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("service/svm0201")
@FunctionId("SVM0201")
public class SVM0201Controller implements RestProcessAware{

    @Resource
    private SVM0201Facade svm0201Facade;

    @PostMapping(value = "/retrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM020101BO> findServiceRequestList(@RequestBody final SVM020101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0201Facade.findServiceRequestList(form);
    }

    @PostMapping(value = "/issue.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void issue(@RequestBody final SVM020101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        svm0201Facade.updateServiceRequest(form);
    }

    @PostMapping(value = "/initial.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM020102Form initial(@RequestBody final SVM020102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0201Facade.initialScreen(form, uc);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SVM020102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm0201Facade.confirm02Screen(form);
    }

    @PostMapping(value = "/freeCoupon.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM020102FreeCouponBO> findFreeCouponList() {

        return svm0201Facade.findCmmServiceDemandVOList();
    }

    @PostMapping(value = "/printPartsClaimTag.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsClaimTag(@RequestBody final SVM020102Form form) {
        return svm0201Facade.printPartsClaimTag(form);
    }

    @PostMapping(value = "/printPartsClaimForBatteryClaimTag.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsClaimForBatteryClaimTag(@RequestBody final SVM020102Form form) {
        return svm0201Facade.printPartsClaimForBatteryClaimTag(form);
    }

}