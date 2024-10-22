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
import com.a1stream.domain.bo.parts.SPQ050801BO;
import com.a1stream.domain.bo.parts.SPQ050802BO;
import com.a1stream.domain.form.parts.SPQ050801Form;
import com.a1stream.domain.form.parts.SPQ050802Form;
import com.a1stream.parts.facade.SPQ0508Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts MI Account Report(Stock)
*
* @author mid2178
*/
@RestController
@RequestMapping("parts/spq0508")
@FunctionId("SPQ0508")
public class SPQ0508Controller implements RestProcessAware {

    @Resource
    private SPQ0508Facade spq0508Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/doPageInitial.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ050802BO> doPageInitial(@RequestBody final SPQ050802Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return spq0508Facade.doPageInitial(form, uc.getDealerCode());
    }

    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final SPQ050802Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SPQ050802BO> datas = spq0508Facade.doPageInitial(form, uc.getDealerCode());
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0508_02, datas, FileConstants.EXCEL_EXPORT_SPQ0508_02);

        return new DownloadResponseView(excel);
    }

    /**
     * spq050801查询
     */
    @PostMapping(value = "/findStockAccountList.json")
    public List<SPQ050801BO> findStockAccountList(@RequestBody final SPQ050801Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spq0508Facade.findStockAccountList(form);
    }

    /**
     * spq050801导出
     */
    @PostMapping(value = "/doDownload01.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload01(@RequestBody final SPQ050801Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SPQ050801BO> datas = spq0508Facade.findStockAccountList(form);
        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0508_01, datas, FileConstants.EXCEL_EXPORT_SPQ0508_01);

        return new DownloadResponseView(excel);
    }
}
