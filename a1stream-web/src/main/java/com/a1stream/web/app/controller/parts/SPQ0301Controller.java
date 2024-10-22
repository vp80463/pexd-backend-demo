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
import com.a1stream.domain.bo.parts.SPQ030101BO;
import com.a1stream.domain.bo.parts.SPQ030101PrintBO;
import com.a1stream.domain.form.parts.SPQ030101Form;
import com.a1stream.parts.facade.SPQ0301Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 *
* 功能描述:Parts Receive And Register Inquiry
*
* mid2287
* 2024年6月6日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/06   Wang Nan      New
 */
@RestController
@RequestMapping("parts/spq0301")
public class SPQ0301Controller implements RestProcessAware {

    @Resource
    private SPQ0301Facade spq0301Facade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/getPartsReceiveList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030101BO> getPartsReceiveList(@RequestBody final SPQ030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0301Facade.getPartsReceiveList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/getPartsReceiveDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030101BO> getPartsReceiveListDetail(@RequestBody final SPQ030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0301Facade.getPartsReceiveListDetail(form, uc.getDealerCode());
    }

    @PostMapping(value = "/getDetailsByPurchaseOrderNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030101BO> getDetailsByPurchaseOrderNo(@RequestBody final SPQ030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spq0301Facade.getDetailsByPurchaseOrderNo(form, uc.getDealerCode());
    }

    @PostMapping(value = "/printPartsReceiveAndRegisterList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsReceiveAndRegisterList(@RequestBody final SPQ030101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPQ030101PrintBO> dataList =
                spq0301Facade.getPartsReceiveAndRegisterPrintList(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPQ0301_01_PARTSSTORINGLISTFORWAREHOUSE, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

}
