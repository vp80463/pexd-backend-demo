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
import com.a1stream.domain.bo.parts.SPQ020101BO;
import com.a1stream.domain.form.parts.SPQ020101Form;
import com.a1stream.parts.facade.SPQ0201Facade;
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
*  1.0    2024/06/24   Liu Chaoran   New
*/
@RestController
@RequestMapping("parts/spq0201")
public class SPQ0201Controller implements RestProcessAware{

    @Resource
    private SPQ0201Facade spq0201Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findSalesOrderCustomerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPQ020101BO> findSalesOrderCustomerList(@RequestBody final SPQ020101Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0201Facade.findSalesOrderCustomerList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/exportSalesOrderCustomerList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView findSalesOrderPartsExportList(@RequestBody final SPQ020101Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0201_01, spq0201Facade.findSalesOrderCustomerExportList(form, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SPQ0201_01);
        return new DownloadResponseView(excel);
    }
}