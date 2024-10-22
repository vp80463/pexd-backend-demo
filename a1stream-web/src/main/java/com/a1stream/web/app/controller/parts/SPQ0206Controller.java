package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ020601BO;
import com.a1stream.domain.bo.parts.SPQ020601PrintBO;
import com.a1stream.domain.bo.parts.SPQ020602BO;
import com.a1stream.domain.form.parts.SPQ020601Form;
import com.a1stream.domain.form.parts.SPQ020602Form;
import com.a1stream.parts.facade.SPM0201Facade;
import com.a1stream.parts.facade.SPQ0206Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Picking Instruction Inquiry
*
* mid2287
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
*/
@RestController
@RequestMapping("parts/spq0206")
public class SPQ0206Controller implements RestProcessAware {

    @Resource
    private SPQ0206Facade spq0206Facade;

    @Resource
    private SPM0201Facade spm0201Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/getPickingInstructionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020601BO> getPickingInstructionList(@RequestBody final SPQ020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spq0206Facade.getPickingInstructionList(form);
    }

    @PostMapping(value = "/exportPickingInstructionExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPQ020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        List<SPQ020601BO> datas = spq0206Facade.getPickingInstructionExportData(form);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0206_01, datas, FileConstants.EXCEL_EXPORT_SPQ0206_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getPickingInstructionDetailList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ020602BO> getPickingInstructionDetailList(@RequestBody final SPQ020602Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0206Facade.getPickingInstructionDetailList(form);
    }

    @PostMapping(value = "/printPartsPickingList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getPartsPickingListReport(@RequestBody final SPQ020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPQ020601PrintBO> dataList =
                spq0206Facade.getPartsPickingListReport(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0206_01_PARTSSTOCKTAKINGRESULT, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

}
