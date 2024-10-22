package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.annotation.FunctionId;
import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.DIM020401BO;
import com.a1stream.domain.form.parts.DIM020401Form;
import com.a1stream.parts.facade.DIM0204Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Unfinished Order Cancel
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/dim0204")
@FunctionId("DIM0204")
public class DIM0204Controller implements RestProcessAware {

    @Resource
    private DIM0204Facade dim0204Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/doSparePartsDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doSparePartsDownload(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<DIM020401BO> datas = dim0204Facade.doSparePartsDownload(form.getPointId(), uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_DIM0204_01_PARTSFASTSALES, datas, FileConstants.EXCEL_EXPORT_DIM0204_01_PARTSFASTSALES);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/doServiceDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doServiceDownload(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<DIM020401BO> datas = dim0204Facade.doServiceDownload(form.getPointId(), uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_DIM0204_01_SERVICE, datas, FileConstants.EXCEL_EXPORT_DIM0204_01_SERVICE);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/doPartsStoringDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doPartsStoringDownload(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<DIM020401BO> datas = dim0204Facade.doPartsStoringDownload(form.getPointId(), uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_DIM0204_01_PARTSSTORING, datas, FileConstants.EXCEL_EXPORT_DIM0204_01_PARTSSTORING);

        return new DownloadResponseView(excel);
    }

    @PostMapping(value = "/doSparePartsCancel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doSparePartsCancel(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0204Facade.doSparePartsCancel(form.getPointId(), uc.getDealerCode());
    }

    @PostMapping(value = "/doServiceCancel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doServiceCancel(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0204Facade.doServiceCancel(form.getPointId(), form.getPointCd(), uc.getDealerCode());
    }

    @PostMapping(value = "/doPartsStoringCancel.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doPartsStoringCancel(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0204Facade.doPartsStoringCancel(form.getPointId(), uc.getDealerCode());
    }

    @PostMapping(value = "/doLocationDelete.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doLocationDelete(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0204Facade.doLocationDelete(form.getPointId(), uc.getDealerCode());
    }

    @PostMapping(value = "/doValidate.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doValidate(@RequestBody final DIM020401Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0204Facade.doValidate(form.getPointId(), uc.getDealerCode());
    }
}
