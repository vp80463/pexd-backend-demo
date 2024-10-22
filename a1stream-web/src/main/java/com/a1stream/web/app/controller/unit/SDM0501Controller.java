package com.a1stream.web.app.controller.unit;

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
import com.a1stream.domain.bo.unit.SDM050101BO;
import com.a1stream.domain.bo.unit.SDM050102BO;
import com.a1stream.domain.form.unit.SDM050101Form;
import com.a1stream.domain.form.unit.SDM050102Form;
import com.a1stream.unit.facade.SDM0501Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Set-up Promotion明细画面
*
* mid2330
* 2024年8月21日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/26   Liu Chaoran   New
*/
@RestController
@RequestMapping("unit/sdm0501")
@FunctionId("SDM0501")
public class SDM0501Controller implements RestProcessAware{

    @Resource
    private SDM0501Facade sdm0501Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findSetUpPromotionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM050101BO> findSetUpPromotionList(@RequestBody final SDM050101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return sdm0501Facade.findSetUpPromotionList(form,uc);
    }

    //export导出
    @PostMapping(value = "/findSetUpPromotionListExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findSetUpPromotionListExport(@RequestBody final SDM050101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDM0501_01, sdm0501Facade.findSetUpPromotionListExport(form,uc), FileConstants.EXCEL_EXPORT_SDM0501_01);
        return new DownloadResponseView(excel);
    }

    //detail、add按钮是否可用
    @PostMapping(value = "/findDetailAndAddFlag.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050101BO findDetailAndAddFlag(@RequestBody final SDM050101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return sdm0501Facade.findDetailAndAddFlag(uc);
    }

    @PostMapping(value = "/findPromotionMC.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM050102BO> findPromotionMC(@RequestBody final SDM050102Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return sdm0501Facade.findPromotionMC(form,uc);
    }

    //export导出
    @PostMapping(value = "/findPromotionMCExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findPromotionMCExport(@RequestBody final SDM050102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_EXPORT_TEMPLATE_SDM0501_02, sdm0501Facade.findPromotionMC(form,uc), FileConstants.EXCEL_EXPORT_SDM0501_02);
        return new DownloadResponseView(excel);
    }

    //校验导入文件
    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050102Form checkFile(@RequestBody final SDM050102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0501Facade.checkFile(form, uc.getDealerCode());
    }

    //更新功能
    @PostMapping(value = "/doConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doConfirm(@RequestBody final SDM050102Form form) {

        sdm0501Facade.doConfirm(form);
    }

    //删除功能
    @PostMapping(value = "/doDelete.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doDelete(@RequestBody final SDM050102Form form) {

        sdm0501Facade.doDelete(form);
    }

    //SDM050202导入文件页面导出错误数据
    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidUploadPromoQCFileDownload(@RequestBody final SDM050102Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = sdm0501Facade.checkFile(form, uc.getDealerCode()).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0501_02, exportList, FileConstants.EXCEL_EXPORT_SDM0501_02);

        return new DownloadResponseView(templateExcel);
    }
}