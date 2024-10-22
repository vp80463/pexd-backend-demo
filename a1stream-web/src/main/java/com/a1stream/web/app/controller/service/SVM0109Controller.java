package com.a1stream.web.app.controller.service;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.service.SVM010901BO;
import com.a1stream.domain.form.service.SVM010901Form;
import com.a1stream.service.facade.SVM0109Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Claim For Battery
*
* mid1341
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   sucao     New
*/
@RestController
@RequestMapping("service/svm0109")
@FunctionId("SVM0109")
public class SVM0109Controller implements RestProcessAware {

    public static final String UPDATE_PROGRAM = "svm0109";

    @Resource
    private SVM0109Facade svm0109Fac;

    @PostMapping(value = "/getOrderDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010901BO getOrderDetailByKey(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        SVM010901BO orderBO = form.getOrderInfo();

        return svm0109Fac.getOrderDetail(orderBO.getPointId(), orderBO.getServiceOrderId(), orderBO.getOrderNo(), uc);
    }

    @PostMapping(value = "/saveOrderInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long saveOrderInfo(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.saveOrderInfo(form, uc);
    }

    @PostMapping(value = "/downloadBattery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010901BO downloadBattery(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.downloadBattery(form.getOrderInfo(), uc);
    }

    @PostMapping(value = "/printServiceJobCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServiceJobCard(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.printServiceJobCard(form.getOrderInfo().getServiceOrderId(), form.getOrderInfo().getLocation(), uc.getUsername());
    }

    @PostMapping(value = "/printServiceJobCardForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServiceJobCardForDO(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.printServiceJobCardForDO(form.getOrderInfo().getServiceOrderId(), form.getOrderInfo().getLocation());
    }

    @PostMapping(value = "/printServicePayment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServicePayment(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.printServicePayment(form.getOrderInfo().getServiceOrderId(), uc.getUsername());
    }

    @PostMapping(value = "/printServicePaymentForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServicePaymentForDO(@RequestBody final SVM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0109Fac.printServicePaymentForDO(form.getOrderInfo().getServiceOrderId());
    }
}