package com.a1stream.web.app.controller.unit;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.unit.SDM050501BO;
import com.a1stream.domain.form.unit.SDM050501Form;
import com.a1stream.unit.facade.SDM0505Facade;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: Upload Certification
*
* mid2303
* 2024年9月10日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/10   Ruan Hansheng   New 
*/
@RestController
@RequestMapping("unit/sdm0505")
public class SDM0505Controller implements RestProcessAware {

    @Resource
    private SDM0505Facade sdm0505Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getInitResult.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050501BO getInitResult(@RequestBody final SDM050501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        return sdm0505Facade.getInitResult(form);
    }

    @PostMapping(value = "/uploadPicture.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadPicture(@RequestBody final SDM050501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        sdm0505Facade.uploadPicture(form);
    }

    @PostMapping(value = "/ajaxResponse.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void ajaxResponse(@RequestBody final SDM050501Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        sdm0505Facade.ajaxResponse(form);
    }

    @PostMapping(value = "/printGiftReceipt.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView printGiftReceipt(@RequestBody final SDM050501Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdm0505Facade.printGiftReceipt(form);
    }
}
