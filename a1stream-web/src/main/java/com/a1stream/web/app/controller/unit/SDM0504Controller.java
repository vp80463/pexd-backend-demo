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
import com.a1stream.domain.bo.unit.SDM050401BO;
import com.a1stream.domain.form.unit.SDM050401Form;
import com.a1stream.unit.facade.SDM0504Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述: YMVN Check Promotion Judgement
*
* mid2303
* 2024年9月3日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name            Comment
*  1.0    2024/09/03   Ruan Hansheng   New
*/
@RestController
@RequestMapping("unit/sdm0504")
public class SDM0504Controller implements RestProcessAware {

    @Resource
    private SDM0504Facade sdm0504Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/getPromotionJudgement.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<SDM050401BO> getPromotionJudgement(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        return sdm0504Facade.getPromotionJudgement(form);
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView export(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        form.setPageFlg(false);
        Page<SDM050401BO> datas = sdm0504Facade.getPromotionJudgement(form);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_EXPORT_SDM0504_01
                                                          , datas.getContent()
                                                          , FileConstants.EXCEL_EXPORT_SDM0504_01);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/getUserType.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form getUserType(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setUserId(uc.getUserId());
        return sdm0504Facade.getUserType(form);
    }

    @PostMapping(value = "/zipXml.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form zipXml(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        form.setSiteId(uc.getDealerCode());
        return sdm0504Facade.zipXml(form);
    }

    @PostMapping(value = "/downloadXml.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form downloadXml(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        return sdm0504Facade.downloadXml(form);
    }

    @PostMapping(value = "/sendToYmvn.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form sendToYmvn(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        return sdm0504Facade.sendToYmvn(form);
    }

    @PostMapping(value = "/approve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form approve(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        return sdm0504Facade.approve(form);
    }

    @PostMapping(value = "/reject.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SDM050401Form reject(@RequestBody final SDM050401Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        
        return sdm0504Facade.reject(form);
    }
}
