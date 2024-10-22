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
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ030601BO;
import com.a1stream.domain.bo.parts.SPQ030601PrintBO;
import com.a1stream.domain.form.parts.SPQ030601Form;
import com.a1stream.parts.facade.SPQ0306Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 *
* 功能描述: Parts Stocktaking List Print
*
* mid2287
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Wang Nan      New
 */
@RestController
@RequestMapping("parts/spq0306")
public class SPQ0306Controller implements RestProcessAware {

    @Resource
    private SPQ0306Facade spq0306Facade;

    @Autowired
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/getPartsStocktakingList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ030601BO> getPartsStocktakingListPageable(@RequestBody final SPQ030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0306Facade.getPartsStocktakingListPageable(form, uc.getDealerCode());
    }

    @PostMapping(value = "/printPartsStocktakingResultList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getPrintPartsStocktakingResultList(@RequestBody final SPQ030601Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPQ030601PrintBO> dataList =
                spq0306Facade.getPrintPartsStocktakingResultList(form, uc.getDealerCode());
        if(CommonConstants.CHAR_ZERO.equals(dataList.get(0).getDisplayFlag())) {
            DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0306_01_PARTSSTOCKTAKINGRESULTLIST, dataList, "");
            return new DownloadResponseView(downloadResponse);
        }else {
            DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0306_01_PARTSSTOCKTAKINGRESULTLISTSYSQTY, dataList, "");
            return new DownloadResponseView(downloadResponse);
        }
    }
}
