package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ030701BO;
import com.a1stream.domain.bo.parts.SPQ030701PrintBO;
import com.a1stream.domain.form.parts.SPQ030701Form;
import com.a1stream.parts.facade.SPQ0307Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking Progress Inquiry
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
*  2.0    2024/06/21   Liu Chaoran   Print
 */
@RestController
@RequestMapping("parts/spq0307")
public class SPQ0307Controller implements RestProcessAware {

    @Resource
    private SPQ0307Facade spq0307Facade;

    @Autowired
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/getPartsStocktakingProgressList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ030701BO> getPartsStocktakingProgressList(@RequestBody final SPQ030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spq0307Facade.getPartsStocktakingProgressList(form);
    }

    /**
     * 功能描述:spq030701 打印功能
     * @author mid2330 Liu Chaoran
     */
    @PostMapping(value = "/printPartsStocktakingProgressList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getPrintPartsStocktakingProgressList(@RequestBody final SPQ030701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SPQ030701PrintBO> dataList =
                spq0307Facade.getPrintPartsStocktakingProgressList(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0307_01_PARTSSTOCKTAKINGPROGRESSLIST, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }
}
