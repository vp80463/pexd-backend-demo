package com.a1stream.web.app.controller.common;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.ReportFacade;
import com.a1stream.common.model.ReportForm;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("common/report")
public class ReportCommonController implements RestProcessAware{

    @Resource
    private ReportFacade reportFacade;

    //PartsPickingList(ByOrder)共通
    @PostMapping(value = "/printPartsPickingListByOrderReport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsPickingListByOrderReport(@RequestBody final ReportForm form, @AuthenticationPrincipal final PJUserDetails uc) {
        return reportFacade.printPartsPickingListByOrderReport(form.getDeliveryOrderId());
    }

    //PartsStoringList(ForWarehouse)共通
    @PostMapping(value = "/printPartsStoringListForWarehouse.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsStoringListForWarehouse(@RequestBody final ReportForm form, @AuthenticationPrincipal final PJUserDetails uc) {
        return reportFacade.printPartsStoringListForWarehouse(form.getReceiptSlipIds());
    }

    @PostMapping(value = "/printPartsSalesReturnInvoiceForFinance.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsSalesReturnInvoiceForFinance(@RequestBody final ReportForm form, @AuthenticationPrincipal final PJUserDetails uc) {
        return reportFacade.printPartsSalesReturnInvoiceForFinance(form.getReturnInvoiceId());
    }
}