package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.parts.SPQ050301BO;
import com.a1stream.domain.form.parts.SPQ050301Form;
import com.a1stream.parts.facade.SPQ0503Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述: Parts MI 查询
*
* @author mid2215
*/
@RestController
@RequestMapping("parts/spq0503")
public class SPQ0503Controller implements RestProcessAware{

    @Resource
    private SPQ0503Facade spq0503Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 查询
     */
    @PostMapping(value = "/findPartsMIList.json")
    public List<SPQ050301BO> findPartsMIList(@RequestBody final SPQ050301Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return spq0503Facade.findPartsMIList(form);
    }

    /**
     * 导出
     */
    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final SPQ050301Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        List<SPQ050301BO> datas = spq0503Facade.findPartsMIList(form);
        DownloadResponse excel = excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0503_01, datas, FileConstants.EXCEL_EXPORT_SPQ0503_01);

        return new DownloadResponseView(excel);
    }
}