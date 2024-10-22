package com.a1stream.web.app.controller.master;

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
import com.a1stream.domain.bo.master.CMM060201BO;
import com.a1stream.domain.bo.master.CMM060202BO;
import com.a1stream.domain.form.master.CMM060201Form;
import com.a1stream.domain.form.master.CMM060202Form;
import com.a1stream.master.facade.CMM0602Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Service Package
*
* @author mid1966
*/
@RestController
@RequestMapping("master/cmm0602")
@FunctionId("CMM0602")
public class CMM0602Controller implements RestProcessAware {

    @Resource
    private CMM0602Facade cmm0602Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getSvPackageData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM060201BO> getSvPackageData(@RequestBody final CMM060201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        return cmm0602Facade.getSvPackageData(model, false);
    }

    @PostMapping(value = "/exportSvPackageData.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportSvPackageData(@RequestBody final CMM060201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        model.setSiteId(uc.getDealerCode());
        List<CMM060201BO> datas = cmm0602Facade.getSvPackageData(model, true);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0602_01, datas, FileConstants.EXCEL_EXPORT_CMM0602_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/initSvPackageInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM060202BO initSvPackageInfo(@RequestBody final CMM060202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0602Facade.initSvPackageInfo(form, uc);
    }

    @PostMapping(value = "/saveSvPackageInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long saveSvPackageInfo(@RequestBody final CMM060202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0602Facade.saveSvPackageInfo(form, uc);
    }
}