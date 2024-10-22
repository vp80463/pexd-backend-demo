package com.a1stream.web.app.controller.service;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.bo.service.SVM010601BO;
import com.a1stream.domain.bo.service.SVM010602BO;
import com.a1stream.domain.bo.service.SVM010604BO;
import com.a1stream.domain.form.service.SVM010601Form;
import com.a1stream.domain.form.service.SVM010602Form;
import com.a1stream.domain.form.service.SVM010604Form;
import com.a1stream.service.facade.SVM0106Facade;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author dong zhen
 */
@RestController
@RequestMapping("service/svm0106")
@FunctionId("SVM0106")
public class SVM0106Controller implements RestProcessAware {

    @Resource
    private SVM0106Facade svm0106Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/retrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010601BO> findServiceRemindList(@RequestBody final SVM010601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        return svm0106Facade.findServiceRemindList(form);
    }

    @PostMapping(value = "/retrieveServiceRemindRecordList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010602BO> findServiceRemindRecordList(@RequestBody final SVM010602Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        return svm0106Facade.findServiceRemindRecordList(form);
    }

    @PostMapping(value = "/serviceFollowUpConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResult serviceFollowUpConfirm(@RequestBody final SVM010602Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        svm0106Facade.serviceFollowUpConfirm(form);
        BaseResult result = new BaseResult();
        result.setMessage(BaseResult.SUCCESS_MESSAGE);
        return result;
    }

    @PostMapping(value = "/export.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportServiceJobList(@RequestBody final SVM010601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        List<SVM010601BO> exportList = svm0106Facade.findServiceRemindExportList(form);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_01, exportList, FileConstants.EXCEL_EXPORT_SVM0106_01);
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/retrieveSalesLeadList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010604BO> retrieveSalesLeadList(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        form.setPointCd(uc.getDefaultPointCd());
        form.setPointId(uc.getDefaultPointId());
        return svm0106Facade.retrieveSalesLeadList(form);
    }

    @PostMapping(value = "/retrieveSalesLeadHistoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SVM010604BO> retrieveSalesLeadHistoryList(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return svm0106Facade.retrieveSalesLeadHistoryList(form);
    }

    @PostMapping(value = "/salesLeadConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void salesLeadConfirm(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        form.setPointCd(uc.getDefaultPointCd());
        form.setPointId(uc.getDefaultPointId());
        svm0106Facade.salesLeadConfirm(form);
    }

    @PostMapping(value = "/salesLeadConfirmExport.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView salesLeadConfirmExport(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        form.setUserId(uc.getUserId());
        form.setPointCd(uc.getDefaultPointCd());
        form.setPointId(uc.getDefaultPointId());
        List<SVM010604BO> exportList = svm0106Facade.retrieveSalesLeadList(form);
        int i = 1;
        for(SVM010604BO lead : exportList){
            lead.setNo(i);
            lead.setEmail2(CommonConstants.CHAR_SPACE);
            i++;
            if (CommonConstants.CHAR_ZERO.equals(form.getCategory())){
                lead.setCategory("FSC");
            } else {
                lead.setCategory("Oil");
            }
        }
        DownloadResponse excel;
        if (CommonConstants.CHAR_ZERO.equals(form.getCategory())){
            excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_04_FSC, exportList, FileConstants.EXCEL_EXPORT_SVM0106_04_FSC);
        } else {
            excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_04_OIL, exportList, FileConstants.EXCEL_EXPORT_SVM0106_04_OIL);
        }
        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public SVM010604Form checkFile(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        if (CollectionUtils.isEmpty(form.getImportList())) {
            return form;
        }
        form.setSiteId(uc.getDealerCode());
        return svm0106Facade.checkFile(form);
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView validFileDownload(@RequestBody final SVM010604Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        var exportList = svm0106Facade.getValidFileList(form);
        DownloadResponse excel;
        if (CommonConstants.CHAR_ZERO.equals(form.getOtherProperty())){
            excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_04_FSC, exportList, FileConstants.EXCEL_EXPORT_SVM0106_04_FSC);
        } else {
            excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SVM0106_04_OIL, exportList, FileConstants.EXCEL_EXPORT_SVM0106_04_OIL);
        }

        return new DownloadResponseView(excel);
    }
}

