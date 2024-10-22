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
import com.a1stream.domain.bo.parts.SPM031001BO;
import com.a1stream.domain.bo.parts.SPM031001PrintBO;
import com.a1stream.domain.form.parts.SPM031001Form;
import com.a1stream.parts.facade.SPM0308Facade;
import com.a1stream.parts.facade.SPM0310Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.exporter.PdfReportExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:cancel or finish stockTaking
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/spm0310")
@FunctionId("SPM0310")
public class SPM0310Controller implements RestProcessAware {

    @Resource
    private SPM0310Facade spm0310Facade;

    @Resource
    private SPM0308Facade spm0308Facade;

    @Resource
    public void setPdfReportExporter(PdfReportExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    private PdfReportExporter pdfExporter;

    @PostMapping(value = "/doRetrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM031001BO> getStockTakingList(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0310Facade.getStockTakingList(form.getPointId(), uc.getDealerCode());
    }

    @PostMapping(value = "/doPrint.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doPrint(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0310Facade.doPrint(form, uc.getDealerCode());
    }

    @PostMapping(value = "/doCancelStockTaking.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doCancelStockTaking(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0310Facade.doCancelStockTaking(form.getPointId(), uc.getDealerCode());
    }

    @PostMapping(value = "/doFinishStockTaking.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doFinishStockTaking(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0310Facade.doFinishStockTaking(form.getPointId(), uc.getDealerCode(), form.getGridDataList(), uc.getPersonId(), uc.getPersonName());
    }

    @PostMapping(value = "/printPartsStocktakingResultStatistics.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsReceiveAndRegisterList(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPM031001PrintBO> dataList =
                spm0310Facade.getPartsReceiveAndRegisterPrintList(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPM0310_01_PARTSSTOCKTAKINGRESULTSTATISTICS, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

    @PostMapping(value = "/printPartsStocktakingGapList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsStocktakingGapList(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPM031001PrintBO> dataList =
                spm0310Facade.getPartsStocktakingGapPrintList(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPM0310_01_PARTSSTOCKTAKINGGAPLIST, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

    @PostMapping(value = "/printPartsStocktakingLedger.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printPartsStocktakingLedger(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPM031001PrintBO> dataList =
                spm0310Facade.getPrintPartsStocktakingLedger(form, uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPM0310_01_PARTSSTOCKTAKINGLEDGER, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }

    //共通
    @PostMapping(value = "/printPartsStocktakingResultList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getPrintPartsStocktakingResultList(@RequestBody final SPM031001Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        List<SPM030801PrintBO> dataList =
                spm0308Facade.getPrintPartsStocktakingResultList(form.getPointId(), uc.getDealerCode());
        DownloadResponse downloadResponse = pdfExporter.generate(FileConstants.JASPER_SPM0308_01_PARTSSTOCKTAKINGRESULTLIST, dataList, "");
        return new DownloadResponseView(downloadResponse);
    }
}
