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
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDM030101BO;
import com.a1stream.domain.bo.unit.SDM030103BO;
import com.a1stream.domain.form.unit.SDM030101Form;
import com.a1stream.domain.form.unit.SDM030103Form;
import com.a1stream.unit.facade.SDM0301Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Sales Order List sdm0301_01明细画面
*
* mid2330
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/19   Liu Chaoran     New
*/
@RestController
@RequestMapping("unit/sdm0301")
@FunctionId("SDM0301")
public class SDM0301Controller implements RestProcessAware{

    @Resource
    private SDM0301Facade sdm0301Fac;

    @Resource
    private ExcelFileExporter excelFileExporter;

    private PdfReportExporter pdfExporter;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    @PostMapping(value = "/listSalesOrderData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM030101BO> listSalesOrderData(@RequestBody final SDM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0301Fac.listSalesOrderData(form, uc, false);
    }

    @PostMapping(value = "/exportSalesOrderData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportSalesOrderData(@RequestBody final SDM030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDM0301_01
                                                        , sdm0301Fac.listSalesOrderData(form, uc, true)
                                                        , FileConstants.EXCEL_EXPORT_SDM0301_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/findDealerWholeSOInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030103BO findDealerWholeSOInfo(@RequestBody final SDM030103Form form) {

        return sdm0301Fac.findDealerWholeSOInfo(form.getOrderId());
    }

    @PostMapping(value = "/printDealerWholeSOInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printDealerWholeSOInfo(@RequestBody final SDM030103Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SDM030103BO> dataList = sdm0301Fac.printDealerWholeSOInfo(form.getOrderId());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SDM0301_03_UNITFASTSALESORDER, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

}