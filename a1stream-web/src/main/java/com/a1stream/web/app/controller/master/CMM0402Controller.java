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
import com.a1stream.domain.bo.master.CMM040201BO;
import com.a1stream.domain.form.master.CMM040201Form;
import com.a1stream.domain.form.master.CMM040202Form;
import com.a1stream.domain.vo.CmmSymptomVO;
import com.a1stream.master.facade.CMM0402Facade;
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
@RequestMapping("master/cmm0402")
@FunctionId("CMM0402")
public class CMM0402Controller implements RestProcessAware {

    @Resource
    private CMM0402Facade cmm0402Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findSectionInfoInquiryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CMM040201BO> findSectionInfoInquiryList(@RequestBody final CMM040201Form form) {

        return cmm0402Facade.findSectionInfoInquiryList(form);
    }

    @PostMapping(value = "/exportSectionInfo.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportServiceJobList(@RequestBody final CMM040201Form form) {

        List<CMM040201BO> datas = cmm0402Facade.findSectionInfoInquiryList(form);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_CMM0402_01, datas, FileConstants.EXCEL_EXPORT_CMM0402_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/findSectionInfoMaintenanceList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CmmSymptomVO> findSectionInfoMaintenanceList(@RequestBody final CMM040202Form form) {

        return cmm0402Facade.findCmmSymptomVOList(form);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM040202Form confirm(@RequestBody final CMM040202Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        cmm0402Facade.confirm(form);
        return form;
    }
}