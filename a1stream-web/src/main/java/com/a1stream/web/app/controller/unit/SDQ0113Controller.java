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
import com.a1stream.domain.bo.unit.SDQ011301BO;
import com.a1stream.domain.bo.unit.SDQ011302BO;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.form.unit.SDQ011302Form;
import com.a1stream.unit.facade.SDQ0113Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Stock Information Inquiry明细画面
*
* mid2330
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Liu Chaoran     New
*/
@RestController
@RequestMapping("unit/sdq0113")
@FunctionId("SDQ0113")
public class SDQ0113Controller implements RestProcessAware{

    @Resource
    private SDQ0113Facade sdq0113Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findStockInformationInquiry.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDQ011301BO> findStockInformationInquiry(@RequestBody final SDQ011301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return sdq0113Facade.findStockInformationInquiry(form);
    }

    //export by model 导出
    @PostMapping(value = "/findStockInfoInquiryExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportStockInfoInquiry(@RequestBody final SDQ011301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0113_01_EXPORTBYMODEL, sdq0113Facade.findStockInformationInquiry(form), FileConstants.EXCEL_EXPORT_SDQ0113_01_EXPORTBYMODEL);
        return new DownloadResponseView(excel);
    }

    //export by motorcycle 导出
    @PostMapping(value = "/findExportByMotorcycle.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findExportByMotorcycle(@RequestBody final SDQ011301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0113_01_EXPORTBYMOTORCYCLE, sdq0113Facade.findExportByMotorcycle(form), FileConstants.EXCEL_EXPORT_SDQ0113_01_EXPORTBYMOTORCYCLE);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/findStockInformationInquiryDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDQ011302BO> findStockInformationInquiryDetail(@RequestBody final SDQ011302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return sdq0113Facade.findStockInformationInquiryDetail(form);
    }
}