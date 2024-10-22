/**
 *
 */
package com.a1stream.web.app.controller.parts;

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
import com.a1stream.domain.bo.parts.SPM020101BO;
import com.a1stream.domain.form.parts.SPM020101Form;
import com.a1stream.parts.facade.SPM020101Facade;
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
@RequestMapping("parts/spm020101")
@FunctionId("SPM020101")
public class SPM020101Controller implements RestProcessAware{

    @Resource
    private SPM020101Facade spm020101Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/searchSalesOrderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPM020101BO> searchSalesOrderList(@RequestBody final SPM020101Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spm020101Facade.searchSalesOrderList(form);
    }

    @PostMapping(value = "/exportSalesOrderListExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPM020101Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        form.setSiteId(uc.getDealerCode());
        Page<SPM020101BO> datas = spm020101Facade.searchSalesOrderList(form);
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPM0201_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SPM0201_01);

        return new DownloadResponseView(excel);
    }

}
