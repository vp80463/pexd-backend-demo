package com.a1stream.web.app.controller.unit;

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
import com.a1stream.domain.bo.unit.SDQ050101BO;
import com.a1stream.domain.form.unit.SDQ050101Form;
import com.a1stream.unit.facade.SDQ0501Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Inquiry Promotion MC List明细画面
*
* mid2330
* 2024年8月21日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/08/21   Liu Chaoran   New
*/
@RestController
@RequestMapping("unit/sdq0501")
@FunctionId("SDQ0501")
public class SDQ0501Controller implements RestProcessAware{

    @Resource
    private SDQ0501Facade sdq0501Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findPromotionMCList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SDQ050101BO> findPromotionMCList(@RequestBody final SDQ050101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return sdq0501Facade.findPromotionMCList(form,uc);
    }

    //export导出
    @PostMapping(value = "/findPromotionMCListExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findPromotionMCListExport(@RequestBody final SDQ050101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0501_01, sdq0501Facade.findPromotionMCListExport(form,uc), FileConstants.EXCEL_EXPORT_SDQ0501_01);
        return new DownloadResponseView(excel);
    }
}