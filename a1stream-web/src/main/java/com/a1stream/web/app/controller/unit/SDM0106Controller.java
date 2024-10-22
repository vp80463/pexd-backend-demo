package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM010601BO;
import com.a1stream.domain.form.unit.SDM010601Form;
import com.a1stream.unit.facade.SDM0106Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/03   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdm0106")
@FunctionId("SDM0106")
public class SDM0106Controller implements RestProcessAware {

    @Resource
    private SDM0106Facade sdm0106Facade;

    @PostMapping(value = "/getFastReceiptReportList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM010601BO> getFastReceiptReportList(@RequestBody final SDM010601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdm0106Facade.getFastReceiptReportList(form);
    }

    @PostMapping(value = "/doReceiptReport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doReceiptReport(@RequestBody final SDM010601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        sdm0106Facade.prepareReceiptReport(form);
    }

}
