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
import com.a1stream.domain.bo.parts.SPQ020201BO;
import com.a1stream.domain.form.parts.SPQ020201Form;
import com.a1stream.parts.facade.SPQ0202Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述:Sales Order Inquiry (By Customer)
*
* mid2330
* 2024年6月24日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/26   Liu Chaoran   New
*/
@RestController
@RequestMapping("parts/spq0202")
public class SPQ0202Controller implements RestProcessAware{

    @Resource
    private SPQ0202Facade spq0202Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findSalesOrderPartsList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020201BO> findSalesOrderPartsList(@RequestBody final SPQ020201Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0202Facade.findSalesOrderPartsList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/findSalesOrderPartsExportList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findSalesOrderPartsExportList(@RequestBody final SPQ020201Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0202_01, spq0202Facade.findSalesOrderPartsExportList(form, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SPQ0202_01);
        return new DownloadResponseView(excel);
    }
}