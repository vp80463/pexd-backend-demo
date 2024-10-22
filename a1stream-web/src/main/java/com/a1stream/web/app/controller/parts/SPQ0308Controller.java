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
import com.a1stream.domain.bo.parts.SPQ030801BO;
import com.a1stream.domain.form.parts.SPQ030801Form;
import com.a1stream.parts.facade.SPQ0308Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Location Usage Inquiry
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/spq0308")
@FunctionId("SPQ0308")
public class SPQ0308Controller implements RestProcessAware {

    @Resource
    private SPQ0308Facade spq0308Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/doRetrieve.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030801BO> doRetrieve(@RequestBody final SPQ030801Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return spq0308Facade.doRetrieve(form, uc.getDealerCode());
    }

    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final SPQ030801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SPQ030801BO> datas = spq0308Facade.doRetrieve(form, uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0308_01, datas, FileConstants.EXCEL_EXPORT_SPQ0308_01);

        return new DownloadResponseView(excel);
    }
}
