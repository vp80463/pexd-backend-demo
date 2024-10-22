package com.a1stream.web.app.controller.master;


import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.master.CMM050901BO;
import com.a1stream.domain.form.master.CMM050901DetailForm;
import com.a1stream.domain.form.master.CMM050901Form;
import com.a1stream.master.facade.CMM0509Facade;
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
@RequestMapping("master/cmm0509")
@FunctionId("CMM0509")
public class CMM0509Controller implements RestProcessAware{

    @Resource
    private CMM0509Facade cmm0509Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/searchPartsDemandList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public PageImpl<CMM050901BO> searchPartsDemandList(@RequestBody final CMM050901Form model,@AuthenticationPrincipal final PJUserDetails uc) {

        return cmm0509Facade.getPartsDemandList(model,uc.getDealerCode());
    }

    @PostMapping(value = "/searchPartsDemand.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM050901BO searchPartsDemandDetail(@RequestBody final CMM050901Form model,@AuthenticationPrincipal final PJUserDetails uc) {

        CMM050901BO bo = cmm0509Facade.getPartsDemandList(model,uc.getDealerCode()).getContent().get(CommonConstants.INTEGER_ZERO);
        bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        return bo;
    }

    @PostMapping(value = "/deletePartsDemand.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePartsDemand(@RequestBody final CMM050901Form model,@AuthenticationPrincipal final PJUserDetails uc) {

        cmm0509Facade.deletePartsDemandList(model,uc.getDealerCode());
    }

    @PostMapping(value = "/editPartsDemand.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editPartsDemand(@RequestBody final CMM050901DetailForm model,@AuthenticationPrincipal final PJUserDetails uc) {

        cmm0509Facade.editPartsDemand(model,uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsDemandExcel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsStockExcel(@RequestBody final CMM050901Form model,@AuthenticationPrincipal final PJUserDetails uc) {
        model.setPageFlg(false);
        PageImpl<CMM050901BO> datas = cmm0509Facade.getPartsDemandList(model,uc.getDealerCode());
        for (CMM050901BO bo: datas) {
            bo.setPartsNo(PartNoUtil.format(bo.getPartsNo()));
        }
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_EXPORT_CMM0509_01, datas, FileConstants.EXCEL_EXPORT_CMM0509_01);

        return new DownloadResponseView(excel);
    }


    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM050901Form checkFile(@RequestBody final CMM050901Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        cmm0509Facade.checkFile(form, uc.getDealerCode());

        return form;
    }

    @PostMapping(value = "/importFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public CMM050901Form importFile(@RequestBody final CMM050901Form form, @AuthenticationPrincipal final PJUserDetails uc) {
        cmm0509Facade.saveFile(form, uc.getDealerCode());

        return form;
    }

    @PostMapping(value = "/validFileDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView validFileDownload(@RequestBody final CMM050901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        var exportList = cmm0509Facade.getValidFileList(form,uc.getDealerCode());
        DownloadResponse templateExcel = excelFileExporter.generate(FileConstants.EXCEL_IMPORT_CMM0509_01, exportList, FileConstants.EXCEL_IMPORT_CMM0509_01);

        return new DownloadResponseView(templateExcel);
    }

}