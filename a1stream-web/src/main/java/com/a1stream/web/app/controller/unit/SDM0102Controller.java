package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.form.unit.SDM010202Form;
import com.a1stream.unit.facade.SDM0102Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Shipping Report
*
* mid2303
* 2024年8月19日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/19   Ruan Hansheng   New
*/
@RestController
@RequestMapping("unit/sdm0102")
public class SDM0102Controller implements RestProcessAware {

    @Resource
    private SDM0102Facade sdm0102Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getScanDetails.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM010202Form getScanDetails(@RequestBody final SDM010202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0102Facade.getScanDetails(form, uc);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM010202Form checkFile(@RequestBody final SDM010202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0102Facade.checkFile(form, uc);
    }

    @PostMapping(value = "/report.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM010202Form report(@RequestBody final SDM010202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0102Facade.reportTransfer(form, uc);
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidFileDownload(@RequestBody final SDM010202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = sdm0102Facade.checkFile(form,uc).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0102_02, exportList, FileConstants.EXCEL_EXPORT_SDM0102_02);

        return new DownloadResponseView(templateExcel);
    }
}
