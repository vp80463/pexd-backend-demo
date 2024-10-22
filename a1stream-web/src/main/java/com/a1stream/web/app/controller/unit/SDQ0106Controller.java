package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDQ010602BO;
import com.a1stream.domain.bo.unit.SDQ010602DetailBO;
import com.a1stream.domain.form.unit.SDQ010602Form;
import com.a1stream.unit.facade.SDQ0106Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Fast Receipt Report (Detail)
*
* mid2287
* 2024年8月28日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/09/06   Wang Nan      New
*/
@RestController
@RequestMapping("unit/sdq0106")
public class SDQ0106Controller implements RestProcessAware {

    @Resource
    private SDQ0106Facade sdq0106Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDQ010602BO getDetail(@RequestBody final SDQ010602Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return sdq0106Facade.getDetail(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SDQ010602Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SDQ010602DetailBO> datas = sdq0106Facade.getDetailList(form);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0106_02, datas, FileConstants.EXCEL_EXPORT_SDQ0106_02);
        return new DownloadResponseView(excel);
    }
}
