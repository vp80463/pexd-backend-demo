package com.a1stream.web.app.controller.parts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.form.parts.SPM040201Form;
import com.a1stream.parts.facade.SPM0402Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@RestController
@RequestMapping("parts/spm0402")
@FunctionId("SPM0402")
public class SPM0402Controller implements RestProcessAware{

    @Resource
    private SPM0402Facade spm0402Facade;

    private ExcelFileExporter exporter;

    @Autowired
    public void setExporter(ExcelFileExporter exporter) {

        this.exporter = exporter;
    }

    @PostMapping(value = "/searchRoqRopList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPM040201BO> searchRoqRopList(@RequestBody final SPM040201Form form,@AuthenticationPrincipal final PJUserDetails uc) {
        return spm0402Facade.searchRoqRopList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/editRoqRopList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editRoqRopList(@RequestBody final SPM040201Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        spm0402Facade.editRoqRopList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/deleteRoqRop.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteRopRoq(@RequestBody final SPM040201Form form) {

        spm0402Facade.deleteRopRoq(form);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM040201Form checkFile(@RequestBody final SPM040201Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        if(ObjectUtils.isEmpty(form.getImportList())) {
            return null;
        }
        spm0402Facade.checkFile(form, uc.getDealerCode());

        return form;
    }

    @PostMapping(value = "/importFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM040201Form importFile(@RequestBody final SPM040201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0402Facade.saveFile(form, uc.getDealerCode());

        return form;
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView validFileDownload(@RequestBody final SPM040201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = spm0402Facade.getValidFileList(form,uc.getDealerCode());
        DownloadResponse templateExcel = exporter.generate(FileConstants.EXCEL_TEMPLATE_SPM0402_01, exportList, FileConstants.EXCEL_TEMPLATE_SPM0402_01);

        return new DownloadResponseView(templateExcel);
    }
}