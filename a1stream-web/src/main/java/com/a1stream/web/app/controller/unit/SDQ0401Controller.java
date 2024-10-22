package com.a1stream.web.app.controller.unit;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.constants.FileConstants;
import com.a1stream.domain.bo.unit.SDQ040103BO;
import com.a1stream.domain.form.unit.SDQ040103Form;
import com.a1stream.unit.facade.SDQ0401Facade;
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
@RequestMapping("unit/sdq0401")
public class SDQ0401Controller implements RestProcessAware{

    @Resource
    private SDQ0401Facade sdq0401Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    /**
     * 查询
     */
    @PostMapping(value = "/getWarrantyCardData.json")
    public Page<SDQ040103BO> getWarrantyCardData(@RequestBody final SDQ040103Form form,@AuthenticationPrincipal final PJUserDetails uc) {

        return sdq0401Facade.pageWarrantyCardInfo(form, uc.getDealerCode());
    }

    /**
     * 导出
     */
    @PostMapping(value = "/downloadWarrantyCardList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView downloadWarrantyCardList(@RequestBody final SDQ040103Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        List<SDQ040103BO> datas = sdq0401Facade.listWarrantyCardInfo(form, uc.getDealerCode());
        DownloadResponse excel =excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SDQ0401_03
                                                         , datas
                                                         , FileConstants.EXCEL_TEMPLATE_SDQ0401_03);

        return new DownloadResponseView(excel);
    }
}