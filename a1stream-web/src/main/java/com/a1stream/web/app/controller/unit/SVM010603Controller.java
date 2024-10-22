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
import com.a1stream.domain.bo.unit.SVM010603BO;
import com.a1stream.domain.form.unit.SVM010603Form;
import com.a1stream.unit.facade.SVM010603Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("unit/svm010603")
@FunctionId("SVM010603")
public class SVM010603Controller {

    @Resource
    private SVM010603Facade svm010603Fac;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getMcSalesLeadList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SVM010603BO> getMcSalesLeadList(@RequestBody final SVM010603Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm010603Fac.pageMcSalesLeadData(form, uc);
    }

    @PostMapping(value = "/getCmmLeadUpdHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010603BO> getCmmLeadUpdHistory(@RequestBody final SVM010603Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm010603Fac.getCmmLeadUpdHistory(form.getLeadManagementResultId());
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportMcSalesLeadList(@RequestBody final SVM010603Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SVM010603BO> datas = svm010603Fac.listMcSalesLeadData(form, uc);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_03, datas, FileConstants.EXCEL_EXPORT_SVM0106_03);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SVM010603Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        svm010603Fac.confirm(form);
    }
}
