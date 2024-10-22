package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDM060101BO;
import com.a1stream.domain.form.unit.SDM060101Form;
import com.a1stream.unit.facade.SDM0601Facade;
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
@RequestMapping("unit/sdm0601")
@FunctionId("SDM0601")
public class SDM0601Controller implements RestProcessAware{

    @Resource
    private SDM0601Facade sdm0601Fac;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 查询
     */
    @PostMapping(value = "/findCusTaxList.json")
    public Page<SDM060101BO> findCusTaxList(@RequestBody final SDM060101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return sdm0601Fac.pageCusTaxList(form);
    }

    /**
     * 保存
     */
    @PostMapping(value = "/saveCusTaxData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveCusTaxData(@RequestBody final SDM060101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        sdm0601Fac.saveCusTaxData(form);
    }

    /**
     * 删除CmmSpecialCompanyTax
     */
    @PostMapping(value = "/deleteCusTax.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteCusTax(@RequestBody final SDM060101Form form) {

        sdm0601Fac.deleteCusTax(form.getTaxId());
    }

    /**
     * 导出
     */
    @PostMapping(value = "/exportCusTax.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportCusTax(@RequestBody final SDM060101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SDM060101BO> datas = sdm0601Fac.listCusTaxList(form);
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDM0601_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SDM0601_01);

        return new DownloadResponseView(excel);
    }

    /**
     * 导入
     */
    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM060101Form checkFile(@RequestBody final SDM060101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        // 上传的数据
        List<SDM060101BO> importList = form.getImportList();
        if (importList.isEmpty()) { return form; }
        sdm0601Fac.checkImport(importList);

        return form;
    }

    /**
     * 导入时 错误数据导出为Excel
     */
    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView getValidFileDownload(@RequestBody final SDM060101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SDM060101BO> importList = form.getImportList();
        var exportList = sdm0601Fac.checkImport(importList);
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_SDM0601_01, exportList, FileConstants.EXCEL_EXPORT_SDM0601_01);

        return new DownloadResponseView(templateExcel);
    }
}