package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDM010901BO;
import com.a1stream.domain.form.unit.SDM010901Form;
import com.a1stream.unit.facade.SDM0109Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer Return By Vehicle
*
* mid2303
* 2024年8月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/29   Ruan Hansheng   New
*/
@RestController
@RequestMapping("unit/sdm0109")
public class SDM0109Controller implements RestProcessAware {

    @Resource
    private SDM0109Facade sdm0109Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getPointList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM010901Form getPointList(@RequestBody final SDM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0109Facade.getPointList(form);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM010901Form checkFile(@RequestBody final SDM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdm0109Facade.checkFile(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SDM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SDM010901BO> importList = form.getImportList();
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_EXPORT_SDM0109_01, importList, FileConstants.EXCEL_EXPORT_SDM0109_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SDM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        sdm0109Facade.confirm(form);
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidFileDownload(@RequestBody final SDM010901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = sdm0109Facade.checkFile(form).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0109_01, exportList, FileConstants.EXCEL_SDM0109_01);

        return new DownloadResponseView(templateExcel);
    }
}
