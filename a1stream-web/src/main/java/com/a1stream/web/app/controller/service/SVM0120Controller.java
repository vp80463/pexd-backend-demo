package com.a1stream.web.app.controller.service;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.service.SVM012001BO;
import com.a1stream.domain.form.service.SVM012001Form;
import com.a1stream.service.facade.SVM0120Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("service/svm0120")
@FunctionId("SVM0120")
public class SVM0120Controller implements RestProcessAware {

    @Resource
    private SVM0120Facade svm0120Facade;

    @PostMapping(value = "/getServiceDetailByIdOrNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM012001BO getServiceDetailByIdOrNo(@RequestBody final SVM012001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0120Facade.getServiceDetailByIdOrNo(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getPointId() ,uc);
    }

    @PostMapping(value = "/newOrModifyServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long newOrModifyServiceOrder(@RequestBody final SVM012001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0120Facade.newOrModifyServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/settleServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long settleServiceOrder(@RequestBody final SVM012001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0120Facade.settleOperationForServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/cancelServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long cancelServiceOrder(@RequestBody final SVM012001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        svm0120Facade.cancelOperationForServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/printBlankJobCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printBlankJobCard(@RequestBody final SVM012001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0120Facade.printBlankJobCard(form.getOrderInfo().getServiceOrderId());
    }

    @PostMapping(value = "/printJobCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printJobCard(@RequestBody final SVM012001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0120Facade.printJobCard(form.getOrderInfo().getServiceOrderId());
    }

    @PostMapping(value = "/printServicePayment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printServicePayment(@RequestBody final SVM012001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0120Facade.printServicePayment(form.getOrderInfo().getServiceOrderId(), uc.getUsername());
    }
}
