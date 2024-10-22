package com.a1stream.web.app.controller.parts;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ020301BO;
import com.a1stream.domain.bo.parts.SPQ020302BO;
import com.a1stream.domain.form.parts.SPQ020301Form;
import com.a1stream.domain.form.parts.SPQ020302Form;
import com.a1stream.parts.facade.SPQ0203Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/28   Ruan Hansheng     New
*/
@RestController
@RequestMapping("parts/spq0203")
@FunctionId("SPQ0203")
public class SPQ0203Controller implements RestProcessAware{

    @Resource
    private SPQ0203Facade spq0203Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getInvoiceList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020301BO> getInvoiceList(@RequestBody final SPQ020301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0203Facade.getInvoiceList(form, uc);
    }

    @PostMapping(value = "/getInvoiceItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020302BO> getInvoiceItemList(@RequestBody final SPQ020302Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0203Facade.getInvoiceItemList(form, uc);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPQ020301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setPageFlg(false);
        Page<SPQ020301BO> datas = spq0203Facade.getInvoiceList(form, uc);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0203_01
                                                         , datas.getContent()
                                                         , FileConstants.EXCEL_TEMPLATE_SPQ0203_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/printFastSalesOrderReportForDO.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printFastSalesOrderReportForDO(@RequestBody final SPQ020301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0203Facade.printFastSalesOrderReportForDO(form.getSalesOrderId(), uc);
    }
}
