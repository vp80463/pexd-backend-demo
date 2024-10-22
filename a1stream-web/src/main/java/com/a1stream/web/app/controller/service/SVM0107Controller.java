package com.a1stream.web.app.controller.service;

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
import com.a1stream.domain.bo.service.SVM010701BO;
import com.a1stream.domain.form.service.SVM010701Form;
import com.a1stream.domain.form.service.SVM010702Form;
import com.a1stream.service.facade.SVM0107Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid1966
*/
@RestController
@RequestMapping("service/svm0107")
@FunctionId("SVM0107")
public class SVM0107Controller implements RestProcessAware {

    @Resource
    private SVM0107Facade svm0107Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/retrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010701BO> findServiceReservationList(@RequestBody final SVM010701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setExportFlag(false);
        return svm0107Facade.findServiceReservationList(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportServiceReservationList(@RequestBody final SVM010701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setExportFlag(true);
        List<SVM010701BO> datas = svm0107Facade.findServiceReservationList(form);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0107_01, datas, FileConstants.EXCEL_EXPORT_SVM0107_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getInfoByPlateNo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010702Form getInfoByPlateNo(@RequestBody final SVM010702Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0107Facade.getInfoByPlateNo(form);
    }

    @PostMapping(value = "/svm010702InitialSearch.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010702Form svm010702InitialSearch(@RequestBody final SVM010702Form form) {

        return svm0107Facade.initial010702Screen(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long confirm(@RequestBody final SVM010702Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return svm0107Facade.confirm(form);
    }
}