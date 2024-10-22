package com.a1stream.web.app.controller.parts;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.domain.form.parts.SPM020103Form;
import com.a1stream.parts.facade.SPM0201Facade;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;


@RestController
@RequestMapping("parts/spm0201")
@FunctionId("SPM0201")
public class SPM0201Controller implements RestProcessAware{

    @Resource
    private SPM0201Facade spm0201Facade;

    @Resource
    private MessageSendManager messageSendManager;

    @PostMapping(value = "/spm020103InitSearch.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form searchRoqRopDefineList(@RequestBody final SPM020103Form form
                                                   ,@AuthenticationPrincipal final PJUserDetails uc) {

        return spm0201Facade.init020103Screen(form.getSalesOrderId(), uc);
    }

    @PostMapping(value = "/saveOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form saveOrder(@RequestBody final SPM020103Form form
                                                   ,@AuthenticationPrincipal final PJUserDetails uc) {

        spm0201Facade.checkConsumer(form, uc);
        spm0201Facade.checkBattery(form, uc);
        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0201Facade.saveOrder(form, uc);

        return form;
    }

    @PostMapping(value = "/picking.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form picking(@RequestBody final SPM020103Form form
                                                   ,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        spm0201Facade.pickingInstruct(form,uc);

        return form;
    }

    @PostMapping(value = "/shipment.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form shipment(@RequestBody final SPM020103Form form
                                                   ,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0201Facade.checkBattery(form, uc);
        spm0201Facade.doShipment(form.getSalesOrderId(), form);

        return form;
    }

    @PostMapping(value = "/cancel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form cancel(@RequestBody final SPM020103Form form
                                                   ,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setPersonId(uc.getPersonId());
        form.setPersonNm(uc.getPersonName());
        spm0201Facade.doCancel(form.getSalesOrderId(), form);

        return form;
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form checkFile(@RequestBody final SPM020103Form form
                                                    ,@AuthenticationPrincipal final PJUserDetails uc) {

        spm0201Facade.checkFile(form, uc);

        return form;
    }

    @PostMapping(value = "/checkBattery.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM020103Form checkBattery(@RequestBody final SPM020103Form form
                                                       ,@AuthenticationPrincipal final PJUserDetails uc) {

        spm0201Facade.checkBattery(form, uc);
        
        return form;
    }

    @PostMapping(value = "/printFastSalesOrderReport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printFastSalesOrderReport(@RequestBody final SPM020103Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spm0201Facade.printFastSalesOrderReport(form.getSalesOrderId());
    }

    @PostMapping(value = "/printFastSalesOrderReportForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printFastSalesOrderReportForDO(@RequestBody final SPM020103Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spm0201Facade.printFastSalesOrderReportForDO(form.getSalesOrderId(), uc);
    }

    @PostMapping(value = "/printPartsSalesInvoiceReportForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsSalesInvoiceReportForDO(@RequestBody final SPM020103Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spm0201Facade.printPartsSalesInvoiceReportForDO(form.getInvoiceIdList(), uc);
    }


}