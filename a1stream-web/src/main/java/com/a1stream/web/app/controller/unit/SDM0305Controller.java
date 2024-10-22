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
import com.a1stream.domain.bo.unit.SDM030501BO;
import com.a1stream.domain.form.unit.SDM030501Form;
import com.a1stream.unit.facade.SDM0305Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Employee Instruction
*
* mid2303
* 2024年10月9日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/10/09   Ruan Hansheng New
*/
@RestController
@RequestMapping("unit/sdm0305")
@FunctionId("SDM0305")
public class SDM0305Controller implements RestProcessAware{

    @Resource
    private SDM0305Facade sdm0305Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getEmployeeInstructionList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SDM030501BO> getEmployeeInstructionList(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdm0305Facade.getEmployeeInstructionList(form);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030501Form checkFile(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdm0305Facade.checkFile(form);

    }

    @PostMapping(value = "/getUserType.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030501Form getUserType(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        return sdm0305Facade.getUserType(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030501Form confirm(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        return sdm0305Facade.confirm(form);
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView validFileDownload(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        var exportList = sdm0305Facade.checkFile(form).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0305_01, exportList, FileConstants.EXCEL_EXPORT_SDM0305_01);

        return new DownloadResponseView(templateExcel);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        List<SDM030501BO> datas = sdm0305Facade.getEmployeeInstructionList(form);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_EXPORT_TEMPLATE_SDM0305_01, datas, FileConstants.EXCEL_EXPORT_SDM0305_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/cancel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM030501Form cancel(@RequestBody final SDM030501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0305Facade.cancel(form);
    }
}