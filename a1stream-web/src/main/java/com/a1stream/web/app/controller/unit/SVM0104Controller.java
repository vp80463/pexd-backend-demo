package com.a1stream.web.app.controller.unit;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SVM010401BO;
import com.a1stream.domain.bo.unit.SVM010402BO;
import com.a1stream.domain.form.unit.SVM010401Form;
import com.a1stream.domain.form.unit.SVM010402Form;
import com.a1stream.unit.facade.SVM0104Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Motorcycle Inquiry
*
* mid2287
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Wang Nan      New
*/
@RestController
@RequestMapping("unit/svm0104")
@FunctionId("SVM0104")
public class SVM0104Controller implements RestProcessAware {

    @Resource
    private SVM0104Facade svm010401Facade;

    @PostMapping(value = "/getMotorcycleList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SVM010401BO> getMotorcycleList(@RequestBody final SVM010401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm010401Facade.getMotorcycleList(form, uc);
    }

    @PostMapping(value = "/getMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010402BO getMotorcycleInfo(@RequestBody final SVM010402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm010401Facade.getMotorcycleInfo(form, uc);
    }

    @PostMapping(value = "/saveMotorcycleInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveMotorcycleInfo(@RequestBody final SVM010402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm010401Facade.saveMotorcycleInfo(form, uc);
    }

    @PostMapping(value = "/getConsumerInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010402BO getConsumerInfo(@RequestBody final SVM010402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm010401Facade.getConsumerInfo(form, uc);
    }

    @PostMapping(value = "/saveConsumerInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveConsumerInfo(@RequestBody final SVM010402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm010401Facade.saveConsumerInfo(form, uc.getDealerCode());
    }
}
