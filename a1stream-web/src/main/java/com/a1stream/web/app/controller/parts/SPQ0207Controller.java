package com.a1stream.web.app.controller.parts;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ020701BO;
import com.a1stream.domain.form.parts.SPQ020701Form;
import com.a1stream.parts.facade.SPQ0207Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述:Parts Cancel History Inquiry
*
* mid2330
* 2024年6月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/25   Liu Chaoran   New
*/
@RestController
@RequestMapping("parts/spq0207")
public class SPQ0207Controller implements RestProcessAware{

    @Resource
    private SPQ0207Facade spq0207Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findPartsCancelHisList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020701BO> findPartsCancelHisList(@RequestBody final SPQ020701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0207Facade.findPartsCancelHisList(form, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsCancelHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findPartsCancelHisExport(@RequestBody final SPQ020701Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0207_01, spq0207Facade.findPartsCancelHisExport(form, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SPQ0207_01);
        return new DownloadResponseView(excel);
    }
}