package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ030501BO;
import com.a1stream.domain.bo.parts.SPQ030501PrintBO;
import com.a1stream.domain.form.parts.SPQ030501Form;
import com.a1stream.parts.facade.SPQ0305Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts On-Working Check List Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@RestController
@RequestMapping("parts/spq0305")
public class SPQ0305Controller implements RestProcessAware {

    @Resource
    private SPQ0305Facade spq0305Facade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/getPartsOnWorkingCheckList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030501BO> getPartsOnWorkingCheckList(@RequestBody final SPQ030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0305Facade.getPartsOnWorkingCheckList(form, uc.getDealerCode());
    }


    @PostMapping(value = "/printPartsOnWorkingCheckList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsOnWorkingCheckList(@RequestBody final SPQ030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPQ030501PrintBO> dataList =
                spq0305Facade.getPartsOnWorkingCheckPrintList(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0305_01_PARTSONWORKINGCHECKLIST, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

}
