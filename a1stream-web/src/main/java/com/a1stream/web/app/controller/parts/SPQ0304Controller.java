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
import com.a1stream.domain.bo.parts.SPQ030401BO;
import com.a1stream.domain.form.parts.SPQ030401Form;
import com.a1stream.parts.facade.SPQ0304Facade;
import com.ymsl.solid.base.io.download.DownloadResponse;
import com.ymsl.solid.report.jxls.ExcelFileExporter;
import com.ymsl.solid.web.download.DownloadResponseView;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
 * @author liu chaoran
 */
@RestController
@RequestMapping("parts/spq0304")
public class SPQ0304Controller implements RestProcessAware{

    @Resource
    private SPQ0304Facade spq0304Facade;

    @Resource
    private ExcelFileExporter excelFileExporter;

    @PostMapping(value = "/findPartsInOutHistoryList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPQ030401BO> findPartsInOutHistoryList(@RequestBody final SPQ030401Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        return spq0304Facade.findPartsInOutHistoryList(model, uc.getDealerCode());
    }

    @PostMapping(value = "/exportPartsInOutHistory.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public DownloadResponseView exportPartsInOutHistory(@RequestBody final SPQ030401Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        DownloadResponse excel =
                excelFileExporter.generate(FileConstants.EXCEL_TEMPLATE_SPQ0304_01, spq0304Facade.exportPartsInOutHistory(model, uc.getDealerCode()), FileConstants.EXCEL_EXPORT_SPQ0304_01);
        return new DownloadResponseView(excel);
    }
}