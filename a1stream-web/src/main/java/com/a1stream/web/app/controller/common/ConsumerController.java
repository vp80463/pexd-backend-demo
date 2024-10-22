package com.a1stream.web.app.controller.common;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.ConsumerFacade;
import com.a1stream.common.model.BaseConsumerForm;
import com.a1stream.domain.bo.common.CmmConsumerBO;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("common/consumer")
public class ConsumerController implements RestProcessAware{

    @Resource
    private ConsumerFacade consumerFacade;

    @PostMapping(value = "/consumerUploadPrivacy.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void consumerUploadPrivacy(@RequestBody final BaseConsumerForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        consumerFacade.consumerUploadPrivacy(form, uc);
    }

    @PostMapping(value = "/saveOrUpdateConsumerInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdateConsumer(@RequestBody final BaseConsumerForm form, @AuthenticationPrincipal final PJUserDetails uc) {

        consumerFacade.saveOrUpdateConsumerInfo(form, uc);
    }

    @PostMapping(value = "/getConsumerDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CmmConsumerBO getConsumerDetail(@RequestBody final BaseConsumerForm model, @AuthenticationPrincipal final PJUserDetails uc) {
        return consumerFacade.getConsumerDetail(model, uc.getDealerCode());
    }

    @PostMapping(value = "/saveConsumerDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long saveConsumerDetail(@RequestBody final CmmConsumerBO model, @AuthenticationPrincipal final PJUserDetails uc) {
        consumerFacade.saveConsumerDetail(model, uc.getDealerCode());
        return model.getConsumerId();
    }
}