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
import com.a1stream.domain.bo.parts.DIM020601BO;
import com.a1stream.domain.form.parts.DIM020601Form;
import com.a1stream.parts.facade.DIM0206Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Stock Import
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/dim0206")
@FunctionId("DIM0206")
public class DIM0206Controller implements RestProcessAware {

    @Resource
    private DIM0206Facade dim0206Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/checkFile.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DIM020601Form checkFile(@RequestBody final DIM020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setPointId(Long.valueOf(form.getOtherProperty().toString()));
        return dim0206Facade.checkFile(form, uc.getDealerCode());
    }

    @PostMapping(value = "/doConfirm.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void doConfirm(@RequestBody final DIM020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        dim0206Facade.doConfirm(form, uc.getDealerCode());
    }

    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final DIM020601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<DIM020601BO> datas = form.getGridDataList();
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_OUT_TEMPLATE_DIM0206_01, datas, FileConstants.EXCEL_EXPORT_DIM0206_01);

        return new DownloadResponseView(excel);
    }
}
