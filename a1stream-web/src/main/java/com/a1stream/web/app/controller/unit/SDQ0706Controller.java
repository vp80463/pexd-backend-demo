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
import com.a1stream.domain.bo.unit.SDQ070601BO;
import com.a1stream.domain.form.unit.SDQ070601Form;
import com.a1stream.unit.facade.SDQ0706Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@RestController
@RequestMapping("unit/sdq0706")
public class SDQ0706Controller implements RestProcessAware{

    @Resource
    private SDQ0706Facade sdq0706Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 查询
     */
    @PostMapping(value = "/findSdPsiDwList.json")
    public Page<SDQ070601BO> findSdPsiDwList(@RequestBody final SDQ070601Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        form.setSiteId(uc.getDealerCode());
        return sdq0706Facade.findSdPsiDwList(form);
    }

    /**
     * 导出
     */
    @PostMapping(value = "/doDownload.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView doDownload(@RequestBody final SDQ070601Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        form.setPageFlg(false);
        form.setSiteId(uc.getDealerCode());
        Page<SDQ070601BO> datas = sdq0706Facade.findSdPsiDwList(form);
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0706_01
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SDQ0706_01);

        return new DownloadResponseView(excel);
    }
}