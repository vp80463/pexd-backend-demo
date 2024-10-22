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
import com.a1stream.domain.bo.parts.SPM040401BO;
import com.a1stream.domain.form.parts.SPM040401Form;
import com.a1stream.domain.form.parts.SPM040402Form;
import com.a1stream.parts.facade.SPM0404Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月14日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/14   Ruan Hansheng     New
*/
@RestController
@RequestMapping("parts/spm0404")
@FunctionId("SPM0404")
public class SPM0404Controller implements RestProcessAware{

    @Resource
    private SPM0404Facade spm0404Facade;

    @PostMapping(value = "/getPurchaseOrderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM040401BO> getPurchaseOrderList(@RequestBody final SPM040401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0404Facade.getPurchaseOrderList(form, uc);
    }

    @PostMapping(value = "/cancelPurchaseOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void cancelPurchaseOrder(@RequestBody final SPM040401Form form) {

        spm0404Facade.cancelPurchaseOrder(form);
    }

    @PostMapping(value = "/getPurchaseOrderItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM040401BO getPurchaseOrderItemList(@RequestBody final SPM040402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0404Facade.getPurchaseOrderItemList(form, uc);
    }

    @PostMapping(value = "/confirmPurchaseOrderItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirmPurchaseOrderItemList(@RequestBody final SPM040402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0404Facade.confirmPurchaseOrderItemList(form, uc);
    }

    @PostMapping(value = "/issuePurchaseOrderItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void issuePurchaseOrderItemList(@RequestBody final SPM040402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0404Facade.issuePurchaseOrderItemList(form, uc);
    }

    @PostMapping(value = "/deletePurchaseOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePurchaseOrder(@RequestBody final SPM040401Form form) {

        spm0404Facade.deletePurchaseOrder(form);
    }
}
