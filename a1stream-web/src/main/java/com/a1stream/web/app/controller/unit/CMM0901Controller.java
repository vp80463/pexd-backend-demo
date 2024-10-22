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
import com.a1stream.domain.bo.unit.CMM090101BO;
import com.a1stream.domain.form.unit.CMM090101Form;
import com.a1stream.unit.facade.CMM0901Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@RestController
@RequestMapping("unit/cmm0901")
@FunctionId("CMM0901")
public class CMM0901Controller implements RestProcessAware{

    @Resource
    private CMM0901Facade cmm0901Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 查询
     */
    @PostMapping(value = "/findMcPriceList.json")
    public List<CMM090101BO> findMcPriceList(@RequestBody final CMM090101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0901Facade.findMcPriceList(form);
    }


    /**
     * 导出
     */
    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final CMM090101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<CMM090101BO> datas = cmm0901Facade.findMcPriceList(form);
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0901_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_CMM0901_01);

        return new DownloadResponseView(excel);
    }

    /**
     * 导入
     */
    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM090101Form checkFile(@RequestBody final CMM090101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0901Facade.checkFile(form);
    }

    /**
     * 保存
     */
    @PostMapping(value = "/editModelPriceList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editModelPriceList(@RequestBody final CMM090101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        cmm0901Facade.editModelPriceList(form);
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidFileDownload(@RequestBody final CMM090101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = cmm0901Facade.checkFile(form).getImportList();
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_CMM0901_01, exportList, FileConstants.EXCEL_EXPORT_CMM0901_01);

        return new DownloadResponseView(templateExcel);
    }
}