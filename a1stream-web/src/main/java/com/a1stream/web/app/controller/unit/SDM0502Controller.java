package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDM050201RetrieveBO;
import com.a1stream.domain.bo.unit.SDM050202RetrieveBO;
import com.a1stream.domain.form.unit.SDM050201Form;
import com.a1stream.domain.form.unit.SDM050202Form;
import com.a1stream.unit.facade.SDM0502Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Set-up Promotion Terms Condition明细画面
*
* mid2330
* 2024年8月29日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/29   Liu Chaoran   New
*/
@RestController
@RequestMapping("unit/sdm0502")
@FunctionId("SDM0502")
public class SDM0502Controller implements RestProcessAware{

    @Resource
    private SDM0502Facade sdm0502Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    //SDM050201页面查询
    @PostMapping(value = "/doRetrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050201RetrieveBO findSetUpPromotionTerms(@RequestBody final SDM050201Form form) {
        return sdm0502Facade.findSetUpPromotionTerms(form);
    }

    //SDM050201页面export导出
    @PostMapping(value = "/doExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findSetUpPromotionTermsExport(@RequestBody final SDM050201Form form) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_EXPORT_TEMPLATE_SDM0502_01, sdm0502Facade.findSetUpPromotionTermsExport(form), FileConstants.EXCEL_EXPORT_SDM0502_01);
        return new DownloadResponseView(excel);
    }

    //SDM050201页面校验导入文件
    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050201Form checkFile(@RequestBody final SDM050201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0502Facade.checkFile(form, uc.getDealerCode());
    }

    //SDM050201导入文件页面导出错误数据
    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidFileDownload(@RequestBody final SDM050201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = sdm0502Facade.checkFile(form,uc.getDealerCode()).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0502_01, exportList, FileConstants.EXCEL_EXPORT_SDM0502_01);

        return new DownloadResponseView(templateExcel);
    }

    //SDM050201页面更新功能
    @PostMapping(value = "/doConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doConfirm(@RequestBody final SDM050201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        sdm0502Facade.doConfirm(form,uc.getDealerCode());
    }

    //SDM050202页面
    //SDM050202页面查询功能
    @PostMapping(value = "/findUploadPromoQCRetrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050202RetrieveBO findUploadPromoQCRetrieve(@RequestBody final SDM050202Form form) {

        return sdm0502Facade.findUploadPromoQC(form);
    }

    //SDM050202页面export导出
    @PostMapping(value = "/doUploadPromoQCExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findUploadPromoQCExport(@RequestBody final SDM050202Form form) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_EXPORT_TEMPLATE_SDM0502_02, sdm0502Facade.findUploadPromoQC(form), FileConstants.EXCEL_EXPORT_SDM0502_02);
        return new DownloadResponseView(excel);
    }

    //SDM050202页面校验导入文件
    @PostMapping(value = "/checkUploadPromoQCFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050202Form checkUploadPromoQCFile(@RequestBody final SDM050202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0502Facade.getCheckUploadPromoQCFile(form, uc.getDealerCode());
    }

    //SDM050202页面更新功能
    @PostMapping(value = "/doConfirmUploadPromoQC.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doConfirmUploadPromoQC(@RequestBody final SDM050202Form form) {

        sdm0502Facade.doConfirmUploadPromoQC(form);
    }

    //SDM050202导入文件页面导出错误数据
    @PostMapping(value = "/validUploadPromoQCFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidUploadPromoQCFileDownload(@RequestBody final SDM050202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = sdm0502Facade.getCheckUploadPromoQCFile(form,uc.getDealerCode()).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0502_02, exportList, FileConstants.EXCEL_EXPORT_SDM0502_02);

        return new DownloadResponseView(templateExcel);
    }
}