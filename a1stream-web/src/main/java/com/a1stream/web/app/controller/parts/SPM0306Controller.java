package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.ReportFacade;
import com.a1stream.domain.bo.parts.SPM030602BO;
import com.a1stream.domain.bo.parts.SPM030603BO;
import com.a1stream.domain.form.parts.SPM030601Form;
import com.a1stream.domain.form.parts.SPM030602Form;
import com.a1stream.domain.form.parts.SPM030603Form;
import com.a1stream.parts.facade.SPM0306Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Parts Point Transfer Instruction
*
* mid2287
* 2024年7月1日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/01   Wang Nan      New
*/
@RestController
@RequestMapping("parts/spm0306")
@FunctionId("SPM0306")
public class SPM0306Controller implements RestProcessAware {

    @Resource
    private SPM0306Facade spm0306Facade;

    @Resource
    private ReportFacade reportFacade;

    @PostMapping(value = "/confirmInstruction.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long confirmPartsPointTransferInstruction(@RequestBody final SPM030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0306Facade.confirmPartsPointTransferInstruction(form);
    }

    @PostMapping(value = "/getPartsPointTransferReceiptList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM030602BO> getPartsPointTransferReceiptList(@RequestBody final SPM030602Form form, @AuthenticationPrincipal final PJUserDetails uc){
        form.setSiteId(uc.getDealerCode());
        return spm0306Facade.getPartsPointTransferReceiptList(form);
    }

    @PostMapping(value = "/confirmPartsPointTransferReceipt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> confirmPartsPointTransferReceipt(@RequestBody final SPM030602Form form, @AuthenticationPrincipal final PJUserDetails uc){
        return spm0306Facade.confirmPartsPointTransferReceipt(form, uc);
    }

    @PostMapping(value = "/getTransferPartsDetailList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM030603BO> getTransferPartsDetailList(@RequestBody final SPM030603Form form, @AuthenticationPrincipal final PJUserDetails uc){
        return spm0306Facade.getTransferPartsDetailList(form);
    }

}
