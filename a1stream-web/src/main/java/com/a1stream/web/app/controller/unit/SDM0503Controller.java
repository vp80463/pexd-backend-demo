package com.a1stream.web.app.controller.unit;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDM050301BO;
import com.a1stream.domain.form.unit.SDM050301Form;
import com.a1stream.unit.facade.SDM0503Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Dealer check Promotion Result
*
* mid2303
* 2024年8月30日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/08/30   Ruan Hansheng   New
*/
@RestController
@RequestMapping("unit/sdm0503")
public class SDM0503Controller implements RestProcessAware {

    @Resource
    private SDM0503Facade sdm0503Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getPromotionResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SDM050301BO> getPromotionResult(@RequestBody final SDM050301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0503Facade.getPromotionResult(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SDM050301Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        form.setPageFlg(false);
        Page<SDM050301BO> datas = sdm0503Facade.getPromotionResult(form);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_EXPORT_SDM0503_01
                                                          , datas.getContent()
                                                          , FileConstants.EXCEL_EXPORT_SDM0503_01);

        return new DownloadResponseView(excel);
    }
}
