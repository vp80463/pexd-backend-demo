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
import com.a1stream.domain.bo.master.CMM010301BO;
import com.a1stream.domain.bo.master.CMM010301ExportBO;
import com.a1stream.domain.form.master.CMM010301Form;
import com.a1stream.domain.form.master.CMM010302Form;
import com.a1stream.master.facade.CMM0103Facade;
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
@RequestMapping("master/cmm0103")
@FunctionId("CMM0103")
public class CMM0103Controller implements RestProcessAware {

    @Resource
    private CMM0103Facade cmm0103Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findConsumerInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM010301BO> findConsumerInfoList(@RequestBody final CMM010301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0103Facade.findConsumerInfoList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/findConsumerExportList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findConsumerExportList(@RequestBody final CMM010301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<CMM010301ExportBO> datas = cmm0103Facade.findConsumerExportList(form, uc);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0103_01, datas, FileConstants.EXCEL_EXPORT_CMM0103_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getConsumerMaintenanceInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM010302Form getConsumerMaintenanceInfo(@RequestBody final CMM010302Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return cmm0103Facade.getConsumerMaintenanceInfo(form);
    }

    @PostMapping(value = "/confirmConsumerInfoList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM010302Form confirmConsumerInfoList(@RequestBody final CMM010302Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return cmm0103Facade.confirmConsumerInfoList(form, uc.getPersonCode());
    }
}