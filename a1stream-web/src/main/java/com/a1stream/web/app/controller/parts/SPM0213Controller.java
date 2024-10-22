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
import com.a1stream.domain.bo.parts.SPM021301BO;
import com.a1stream.domain.bo.parts.SPM021302BO;
import com.a1stream.domain.form.parts.SPM021301Form;
import com.a1stream.domain.form.parts.SPM021302Form;
import com.a1stream.parts.facade.SPM0213Facade;
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
@RequestMapping("parts/spm0213")
@FunctionId("SPM0213")
public class SPM0213Controller implements RestProcessAware {

    @Resource
    private SPM0213Facade spm0213Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/searchBackOrderList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SPM021301BO> searchBackOrderList(@RequestBody final SPM021301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0213Facade.searchBackOrderList(form);
    }

    @PostMapping(value = "/boRelease.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void boRelease(@RequestBody final SPM021301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        spm0213Facade.boRelease(form);
    }

    @PostMapping(value = "/exportBackOrderExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final SPM021301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setPageFlg(false);
        form.setSiteId(uc.getDealerCode());
        Page<SPM021301BO> datas = spm0213Facade.searchBackOrderList(form);
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPM0213_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SPM0213_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getBackOrderDetail.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM021302BO getBackOrderDetail(@RequestBody final SPM021302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        return spm0213Facade.getBackOrderDetail(form);
    }

    @PostMapping(value = "/editBackOrder.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editBackOrder(@RequestBody final SPM021302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        form.setSiteId(uc.getDealerCode());
        spm0213Facade.editBackOrder(form);
    }

    @PostMapping(value = "/searchPurchaseStatus.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SPM021302BO searchPurchaseStatus(@RequestBody final SPM021302Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        return spm0213Facade.searchPurchaseStatus(form, uc);

    }
}
