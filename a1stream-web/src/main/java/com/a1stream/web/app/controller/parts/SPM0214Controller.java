package com.a1stream.web.app.controller.parts;

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
import com.a1stream.domain.bo.parts.SPM021401BO;
import com.a1stream.domain.bo.parts.SPM021402BO;
import com.a1stream.domain.form.parts.SPM021401Form;
import com.a1stream.domain.form.parts.SPM021402Form;
import com.a1stream.parts.facade.SPM0214Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;
/**
* 功能描述:
*
* MID2303
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/24   Ruan Hansheng     New
*/
@RestController
@RequestMapping("parts/spm0214")
@FunctionId("SPM0214")
public class SPM0214Controller implements RestProcessAware {

    @Resource
    private SPM0214Facade spm0214Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getReturnRequestListList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM021401BO> getReturnRequestListList(@RequestBody final SPM021401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0214Facade.getReturnRequestListList(form, uc);
    }

    @PostMapping(value = "/getReturnRequestItemList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM021401BO getReturnRequestItemList(@RequestBody final SPM021402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0214Facade.getReturnRequestItemList(form, uc);
    }

    @PostMapping(value = "/confirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void confirm(@RequestBody final SPM021402Form form) {

        spm0214Facade.confirm(form);
    }

    @PostMapping(value = "/issue.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void issue(@RequestBody final SPM021402Form form) {

        spm0214Facade.issue(form);
    }

    @PostMapping(value = "/picking.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM021402Form picking(@RequestBody final SPM021402Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0214Facade.picking(form, uc);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SPM021402Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        SPM021401BO bo = spm0214Facade.getReturnRequestItemList(form, uc);
        List<SPM021402BO> datas = bo.getTableDataList();
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPM0214_02, datas, FileConstants.EXCEL_EXPORT_SPM0214_02);
        return new DownloadResponseView(excel);
    }
}
