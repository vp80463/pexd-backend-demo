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
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPM030801PrintBO;
import com.a1stream.domain.form.parts.SPM030801Form;
import com.a1stream.parts.facade.SPM0308Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:start stockTaking
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/spm0308")
@FunctionId("SPM0308")
public class SPM0308Controller implements RestProcessAware {

    @Resource
    private SPM0308Facade spm0308Facade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/doStartStockTaking.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doStartStockTaking(@RequestBody final SPM030801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0308Facade.doStartStockTaking(form.getPointId(), form.getStockTakingRange(), uc.getDealerCode());
    }

    @PostMapping(value = "/printPartsStocktakingResultList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getPrintPartsStocktakingResultList(@RequestBody final SPM030801Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPM030801PrintBO> dataList =
                spm0308Facade.getPrintPartsStocktakingResultList(form.getPointId(), uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPM0308_01_PARTSSTOCKTAKINGRESULTLIST, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }
}
