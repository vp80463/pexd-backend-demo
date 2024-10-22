package com.a1stream.web.app.controller.service;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.service.SVM013001BO;
import com.a1stream.domain.bo.service.SpecialClaimBO;
import com.a1stream.domain.form.service.SVM013001Form;
import com.a1stream.service.facade.SVM0130Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("service/svm0130")
@FunctionId("SVM0130")
public class SVM0130Controller implements RestProcessAware {

    @Resource
    private SVM0130Facade svm0130Facade;

    @PostMapping(value = "/getServiceDetailByIdOrNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM013001BO getServiceDetailByIdOrNo(@RequestBody final SVM013001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0130Facade.getServiceDetailByIdOrNo(model.getOrderInfo().getServiceOrderId(), model.getOrderInfo().getOrderNo(), model.getOrderInfo().getPointId(), model.getOrderInfo().getFrameNo() ,uc);
    }

    @PostMapping(value = "/newOrModifyServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long newOrModifyServiceOrder(@RequestBody final SVM013001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0130Facade.newOrModifyServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/settleServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long settleServiceOrder(@RequestBody final SVM013001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        svm0130Facade.settleOperationForServiceOrder(model, uc);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/cancelServiceOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long cancelServiceOrder(@RequestBody final SVM013001Form model) {

        svm0130Facade.cancelOperationForServiceOrder(model);

        return model.getOrderInfo().getServiceOrderId();
    }

    @PostMapping(value = "/getMotorDetailByPlateOrFrame.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM013001BO getMotorDetailByPlateOrFrame(@RequestBody final SVM013001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0130Facade.getMotorDetailByPlateOrFrame(model.getOrderInfo());
    }

    @PostMapping(value = "/getSpecialClaimDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SpecialClaimBO getSpecialClaimDetail(@RequestBody final SVM013001Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0130Facade.getSpecialClaimDetail(model.getOrderInfo().getSpecialClaimId(), model.getOrderInfo().getCmmSerializedProductId(), model.getOrderInfo().getModelCd(), uc.getTaxPeriod());
    }

    @PostMapping(value = "/printJobCard.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView print0KmJobCard(@RequestBody final SVM013001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0130Facade.print0KmJobCard(form.getOrderInfo().getServiceOrderId());
    }

    @PostMapping(value = "/printServicePayment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView print0KmServicePayment(@RequestBody final SVM013001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return svm0130Facade.print0KmServicePayment(form.getOrderInfo().getServiceOrderId());
    }

}
